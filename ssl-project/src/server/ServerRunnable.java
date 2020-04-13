package server;

import AuthenticationCode.MAC;
import encryption.DESEncrypt;
import encryption.Encrypt;
import encryption.TripleDESEncrypt;
import AuthenticationCode.SignatureGenVerify;
import encryption.AESEncrypt;
import hello.ServerHello;
import models.handshake.CertificateRequest;
import models.handshake.MessageType;
import models.handshake.CertificateType;
import models.handshake.Message;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.security.PrivateKey;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.ReadWriteHelper;
import utils.X509CertificateManager;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import models.handshake.*;
import models.recordprotocol.MessageRecordLayer;
import utils.ReadWriteRecordLayer;
import utils.ServerClientKeys;

/**
 *
 * @author Richard
 */
public class ServerRunnable implements Runnable {

    private final Socket socket;
    private static final String SERVER_CERT = "certificates/server.cer";
    private final ArrayList<String> authorities = new ArrayList<>(Arrays.asList("any", "Project Client", "coe817-client", "coe817"));
    private X509Certificate clientCertificate;
    private ClientKeyExchange clientKeyExchange;
    private CertificateVerify certificateVerify;
    private Finished clientFinished;

    public ServerRunnable(Socket socket) {
        System.out.println("Creating a runnable thread of the Server");
        this.socket = socket;
    }

    @Override
    public void run() {
        X509CertificateManager certManager = new X509CertificateManager();

        try {
            DataInputStream in = new DataInputStream(
                    new BufferedInputStream(socket.getInputStream()));
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            ReadWriteHelper rWHelper = new ReadWriteHelper();
            HandShakeMessages handShakeMessages = new HandShakeMessages();

            /// PHASE 1
            System.out.println("\nBeginning Phase 1");

            // create Cipher Suite and 
            ArrayList<String> keyEx = new ArrayList<>(), cipherAlg = new ArrayList<>(), macAlg = new ArrayList<>();

            // add Algorithms to Cipher Suite in decreasing order of preferance
            keyEx.add("RSA");
            keyEx.add("DH");
            cipherAlg.add("DES");
            cipherAlg.add("3DES");
            cipherAlg.add("AES");
            macAlg.add("MD5");
            macAlg.add("SHA-1");

            // receive client hello, then send server_hello
            ServerHello sHello = new ServerHello(in, out, keyEx, cipherAlg, macAlg);
            sHello.init();

            // Debugging Information
            System.out.println("\nSSL Version = " + sHello.getSSLVersion());
            System.out.println("Nonce Client = " + Arrays.toString(sHello.getRandomFromClient()));
            System.out.println("\nNonce Server = " + Arrays.toString(sHello.getRandom()));
            System.out.println("\nKey Exchange Algorithm = " + sHello.getPreferredKeyExchange());
            System.out.println("Cipher Algorithm = " + sHello.getPreferredCipherAlg());
            System.out.println("MAC Algorithm = " + sHello.getPreferredMACAlg());

            //// PHASE 2
            // SENDING CERTIFICATE
            System.out.println("\nBeginning Phase 2");
            //get path to the file
            Path path = FileSystems.getDefault().getPath(SERVER_CERT);
            String fileLocation = path.toAbsolutePath().toString();
            System.out.println("FileLocation: " + fileLocation);
            //get cert from the path
            X509Certificate serverCert = certManager.getCertificateFromFile(fileLocation);

            byte[] serverCertBytes = certManager.getEncodedCertificate(serverCert);
            Message messageObject = new Message(MessageType.certificate, serverCertBytes);
            // set handshakemessage
            handShakeMessages.setServerCertificate(messageObject);
            // certificate send
            rWHelper.writeMessage(out, messageObject);
            System.out.println("Sent Client Server-Certificate");

            // SENDING CERTIFICATE REQUEST
            // create certificate request 
            CertificateRequest certificateRequest = new CertificateRequest(CertificateType.rsa_signature_only, this.authorities);
            byte[] certificateRequestBytes = rWHelper.serializeObject(certificateRequest);
            messageObject = new Message(MessageType.certificate_request, certificateRequestBytes);
            rWHelper.writeMessage(out, messageObject);
            System.out.println("Sent Client Certificate Request");
            // set handshakemessage
            handShakeMessages.setCertificateRequest(messageObject);
            // SENDING SERVER HELLO DONE
            // send server done message
            messageObject = new Message(MessageType.server_done, null);
            rWHelper.writeMessage(out, messageObject);
            System.out.println("Sent Client Server Done");
            // set handshakemessage
            handShakeMessages.setServerHelloDone(messageObject);
            /// PHASE 3 
            System.out.println("\nBeginning Phase 3");

            boolean done = false;
            while (!done) {
                // read message from the server
                messageObject = rWHelper.readMessage(in);
                int typeInt = messageObject.getMessageType().ordinal();
                switch (typeInt) {
                    case 3:
                        // certificate
                        byte[] certBytes = messageObject.getContent();
                        this.clientCertificate = certManager.getCertificateFromBytes(certBytes);
                        System.out.println("Received Client Certificate");
                        // set handshakemessage
                        handShakeMessages.setClientCertificate(messageObject);
                        break;
                    case 7:
                        // certificate verifiy
                        this.certificateVerify = (CertificateVerify) rWHelper.deserialize(messageObject.getContent());
                        System.out.println("Certificate Verifiy");
                        //verifiy certificate
                        SignatureGenVerify sig = new SignatureGenVerify();
                        byte[] handShakeMessagesBytes = rWHelper.serializeObject(handShakeMessages);

                        boolean verify = sig.verifySignature(this.clientCertificate.getPublicKey(), handShakeMessagesBytes, certificateVerify.getSignature());
                        System.out.println("Checking signature validity:  " + verify);

                        // set handshakemessage
                        handShakeMessages.setCertificateVerify(messageObject);
                        // after certificate verify comes change cipher spec so read the 1 byte
                        byte changeCipherSpec = in.readByte();
                        break;
                    case 8:
                        // client_key_exhcnage
                        System.out.println("\nBeginning Phase 4");
                        this.clientKeyExchange = (ClientKeyExchange) rWHelper.deserialize(messageObject.getContent());
                        System.out.println("Client Key Exchange. Premaster secret: " + new String(this.clientKeyExchange.getParameters(), StandardCharsets.UTF_8));
                        // set handshakemessage
                        handShakeMessages.setClientKeyExchange(messageObject);
                        break;
                    case 9:
                        // server_done 
                        this.clientFinished = (Finished) rWHelper.deserialize(messageObject.getContent());
                        System.out.println("Recieved Finished");
                        done = true;
                        sig = new SignatureGenVerify();
                        handShakeMessagesBytes = rWHelper.serializeObject(handShakeMessages);

                        verify = sig.verifySignature(this.clientCertificate.getPublicKey(), handShakeMessagesBytes, this.clientFinished.getHashValue());
                        System.out.println("Checking hash SERVER_PRIV:  " + verify);
                        break;
                    default:
                }
            }

            byte[] masterSecret = MAC.generateMaster(this.clientKeyExchange.getParameters(), sHello.getRandomFromClient(), sHello.getRandom());

            //// PHASE 4
            // Change Cipher Spec sends only a single byte with a value of 1
            out.writeByte(1);

            byte[] handShakeMessagesBytes = rWHelper.serializeObject(handShakeMessages);
            SignatureGenVerify sig = new SignatureGenVerify();
            ServerClientKeys serverClientKeys = new ServerClientKeys();
            PrivateKey keyOfCertificate = serverClientKeys.getServerPrivateKey();
            byte[] hash = sig.signContent(keyOfCertificate, handShakeMessagesBytes);

            Finished finished = new Finished(hash);
            byte[] finishedBytes = rWHelper.serializeObject(finished);
            messageObject = new Message(MessageType.finished, finishedBytes);
            rWHelper.writeMessage(out, messageObject);
            System.out.println("Sent Finished");

            // RECORD LAYER
            System.out.println("\nRECORD LAYER");

            // begin one way chat
            chat(in, out, masterSecret, sHello.getPreferredCipherAlg());

        } catch (Exception ex) {
            Logger.getLogger(ServerRunnable.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                socket.close();
                System.out.println("Closed Socket");

            } catch (IOException ex) {
                Logger.getLogger(ServerRunnable.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void chat(DataInputStream in, DataOutputStream out, byte[] masterSecret, String cipherAlg) {
       
        Encrypt ciph = null;
        
        switch (cipherAlg) {
            case "DES":
                ciph = new DESEncrypt(masterSecret);
                break;
            case "3DES":
                ciph = new TripleDESEncrypt(masterSecret);
                break;
            case "AES":
                ciph = new AESEncrypt(masterSecret);
                break;
            default:
                ciph = new DESEncrypt(masterSecret);
        }
        
        while (true) {
            try {
                ReadWriteRecordLayer rWRecordLayer = new ReadWriteRecordLayer();
                if (in.available() != 0) {
                    MessageRecordLayer messageRecord = rWRecordLayer.readMessage(in);
                    System.out.println("Received Message: " + ciph.decrypt(new String(messageRecord.getContent(), StandardCharsets.UTF_8)));
                    if (ciph.decrypt(new String(messageRecord.getContent(), StandardCharsets.UTF_8)).equals("/end")){
                        break;
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(ServerRunnable.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
