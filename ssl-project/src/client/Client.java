package client;

import AuthenticationCode.MAC;
import encryption.DESEncrypt;
import AuthenticationCode.SignatureGenVerify;
import encryption.AESEncrypt;
import encryption.Encrypt;
import hello.ClientHello;
import encryption.RSAEncrypt;
import encryption.TripleDESEncrypt;
import models.handshake.CertificateRequest;
import models.handshake.MessageType;
import models.handshake.CertificateVerify;
import models.handshake.Message;
import models.handshake.ClientKeyExchange;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.handshake.*;
import utils.ReadWriteHelper;
import utils.ReadWriteRecordLayer;
import utils.ServerClientKeys;
import utils.X509CertificateManager;

/**
 *
 * @author Richard
 */
public class Client {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 5000;
    private static final String CLIENT_CERT = "certificates/client.cer";
    private X509Certificate serverCertificate;
    private CertificateRequest certificateRequest;
    private String clientPrivateKey = "clientKey";

    public Client() {
    }

    public void main() {
        Message messageObject;
        ReadWriteHelper rWHelper = new ReadWriteHelper();
        X509CertificateManager certManager = new X509CertificateManager();

        try {
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            System.out.println("Connected to Server");
            DataInputStream in = new DataInputStream(
                    new BufferedInputStream(socket.getInputStream()));
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            // prepare the handshake messages
            HandShakeMessages handShakeMessages = new HandShakeMessages();
            //// PHASE 1
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

            // send client hello, then receive server_hello 
            ClientHello cHello = new ClientHello(in, out, keyEx, cipherAlg, macAlg);
            cHello.init();

            //Dubugging information
            System.out.println("\nSSL Version = " + cHello.getSSLVersion());
            System.out.println("Nonce Client = " + Arrays.toString(cHello.getRandom()));
            System.out.println("\nNonce Server = " + Arrays.toString(cHello.getRandomFromServer()));
            System.out.println("\nKey Exchange Algorithm = " + cHello.getPreferredKeyExchange());
            System.out.println("Cipher Algorithm = " + cHello.getPreferredCipherAlg());
            System.out.println("MAC Algorithm = " + cHello.getPreferredMACAlg());

            /// PHASE 2
            System.out.println("\nBeginning Phase 2");
            boolean serverDone = false;
            while (!serverDone) {
                // read message from the server
                messageObject = rWHelper.readMessage(in);
                int typeInt = messageObject.getMessageType().ordinal();
                switch (typeInt) {
                    case 3:
                        // certificate
                        byte[] certBytes = messageObject.getContent();
                        this.serverCertificate = certManager.getCertificateFromBytes(certBytes);
                        // set server certificate in handshake
                        handShakeMessages.setServerCertificate(messageObject);
                        System.out.println("Recieved Server Certificate");
                        break;
                    case 5:
                        // certificate_request
                        this.certificateRequest = (CertificateRequest) rWHelper.deserialize(messageObject.getContent());
                        // set server certificate in handshake
                        handShakeMessages.setCertificateRequest(messageObject);
                        System.out.println("CertificateRequest. Type: " + certificateRequest.getCertificateType() + ", Certificate Authorities: " + certificateRequest.getCertificateAuthorities().toString());
                        break;
                    case 6:
                        // server_done 
                        System.out.println("Phase2 Server Done");
                        // set hello done
                        handShakeMessages.setServerHelloDone(messageObject);
                        serverDone = true;
                        break;
                    default:
                }
            }

            //// PHASE 3 
            System.out.println("\nBeginning Phase 3");
            /// Certificate
            //get cert from the path
            Path path = FileSystems.getDefault().getPath(CLIENT_CERT);
            String fileLocation = path.toAbsolutePath().toString();
            System.out.println("FileLocation: " + fileLocation);
            X509Certificate clientCert = certManager.getCertificateFromFile(fileLocation);
            byte[] serverCertBytes = certManager.getEncodedCertificate(clientCert);
            messageObject = new Message(MessageType.certificate, serverCertBytes);
            //set handshake message
            handShakeMessages.setClientCertificate(messageObject);
            // certificate send
            rWHelper.writeMessage(out, messageObject);
            System.out.println("Sent Server Client-Certificate");

            /**
             * Add the parameters to the client key exchange 
             */
            RSAEncrypt client = new RSAEncrypt();
            byte[] serverPublicKey = this.serverCertificate.getPublicKey().getEncoded();
            client.setReceiverPublicKey(serverPublicKey);

            ClientKeyExchange clientKeyExchange = new ClientKeyExchange();
            byte[] preMasterSecret = clientKeyExchange.generatePremasterSecret();
            clientKeyExchange.setParameters(preMasterSecret);
            System.out.println("Premaster secret: " + new String(preMasterSecret, StandardCharsets.UTF_8));
            byte[] keyExchangeBytes = rWHelper.serializeObject(clientKeyExchange);
            messageObject = new Message(MessageType.client_key_exchange, keyExchangeBytes);
            rWHelper.writeMessage(out, messageObject);
            System.out.println("Sent Client Key Exchange");

            byte[] masterSecret = MAC.generateMaster(preMasterSecret, cHello.getRandom(), cHello.getRandomFromServer());

            //set handshake message
            handShakeMessages.setClientKeyExchange(messageObject);

            // create signature from the handShakeMessages
            byte[] handShakeMessagesBytes = rWHelper.serializeObject(handShakeMessages);
            SignatureGenVerify sig = new SignatureGenVerify();
            ServerClientKeys serverClientKeys = new ServerClientKeys();
            PrivateKey keyOfCertificate = serverClientKeys.getClientPrivateKey();
            byte[] signature = sig.signContent(keyOfCertificate, handShakeMessagesBytes);
            CertificateVerify certificateVerify = new CertificateVerify(signature);
            byte[] certificateVerifyBytes = rWHelper.serializeObject(certificateVerify);
            messageObject = new Message(MessageType.certificate_verify, certificateVerifyBytes);
            rWHelper.writeMessage(out, messageObject);
            System.out.println("Sent Certificate Verify");

            //set handshake for after the verify
            handShakeMessages.setCertificateVerify(messageObject);
            //// PHASE 4
            System.out.println("\nBeginning Phase 4");
            // Change Cipher Spec sends only a single byte with a value of 1
            out.writeByte(1);

            // add hash and sig
            byte[] handShakeMessagesByte = rWHelper.serializeObject(handShakeMessages);
            byte[] hash = sig.signContent(keyOfCertificate, handShakeMessagesByte);
            Finished finished = new Finished(hash);
            byte[] finishedBytes = rWHelper.serializeObject(finished);
            messageObject = new Message(MessageType.finished, finishedBytes);
            rWHelper.writeMessage(out, messageObject);
            System.out.println("Sent Finished");

            // recieve the change cipher spec
            byte changeCipherSpec = in.readByte();
            messageObject = rWHelper.readMessage(in);
            int typeInt = messageObject.getMessageType().ordinal();

            if (typeInt == 9) {
                Finished serverFinished = (Finished) rWHelper.deserialize(messageObject.getContent());
                System.out.println("Recieved Finished");
                handShakeMessagesBytes = rWHelper.serializeObject(handShakeMessages);
                boolean verify = sig.verifySignature(this.serverCertificate.getPublicKey(), handShakeMessagesBytes, serverFinished.getHashValue());
                System.out.println("Finished hash verify: " + verify);
            }

            // RECORD LAYER
            System.out.println("\nRECORD LAYER");
            System.out.println("Type '/end' to end chat");
            // begin one way chat
            chat(in, out, masterSecret, cHello.getPreferredCipherAlg());

            socket.close();
        } catch (IOException | InvalidKeySpecException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
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

        Scanner input = new Scanner(System.in);
        ReadWriteRecordLayer rWRecordLayer = new ReadWriteRecordLayer();

        while (input.hasNextLine()) {
            String message = input.nextLine();
            rWRecordLayer.writeApplicationBytes(out, ciph.encrypt(message).getBytes());
            if (message.equals("/end")){
                break;
            }
        }
    }

}
