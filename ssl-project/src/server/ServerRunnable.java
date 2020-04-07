package server;

import models.handshake.CertificateRequest;
import models.handshake.MessageType;
import models.handshake.CertificateType;
import models.handshake.Message;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.ReadWriteHelper;
import utils.X509CertificateManager;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import models.handshake.*;

/**
 *
 * @author Richard
 */
public class ServerRunnable implements Runnable {

    private final Socket socket;
    private static final String SERVER_CERT = "certificates/server.cer";
    private final ArrayList<String> authorities = new ArrayList<>(Arrays.asList("any", "Project Client", "coe817-client", "coe817"));
    private X509Certificate clientCertificate;
    private KeyExchange clientKeyExchange;
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
            /// PHASE 1
            Message messageObject = new Message(MessageType.server_hello, null);
            rWHelper.writeMessage(out, messageObject);

            //// PHASE 2
            // SENDING CERTIFICATE
            System.out.println("Beginning Phase 2");
            //get path to the file
            Path path = FileSystems.getDefault().getPath(SERVER_CERT);
            String fileLocation = path.toAbsolutePath().toString();
            System.out.println("FileLocation: " + fileLocation);
            //get cert from the path
            X509Certificate serverCert = certManager.getCertificateFromFile(fileLocation);

            byte[] serverCertBytes = certManager.getEncodedCertificate(serverCert);
            messageObject = new Message(MessageType.certificate, serverCertBytes);
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

            // SENDING SERVER HELLO DONE
            // send server done message
            messageObject = new Message(MessageType.server_done, null);
            rWHelper.writeMessage(out, messageObject);
            System.out.println("Sent Client Server Done");

            /// PHASE 3 
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
                        System.out.println("Recieved Client Certificate");
                        break;
                    case 7:
                        // certificate verifiy
                        this.certificateVerify = (CertificateVerify) rWHelper.deserialize(messageObject.getContent());
                        System.out.println("Certificate Verifiy");

                        // after certificate verify comes change cipher spec so read the 1 byte
                        int changeCipherSpec = in.read();
                        break;
                    case 8:
                        // client_key_exhcnage
                        this.clientKeyExchange = (KeyExchange) rWHelper.deserialize(messageObject.getContent());
                        System.out.println("Client Key Exchange");
                        break;
                    case 9:
                        // server_done 
                        this.clientFinished = (Finished) rWHelper.deserialize(messageObject.getContent());
                        System.out.println("Recieved Finished");
                        done = true;
                        break;
                    default:
                }
            }

            //// PHASE 4
            // Change Cipher Spec sends only a single byte with a value of 1
            out.write(1);

            /**
             * Add the parameters to the Finished modify the model Finished to
             * have the proper parameters
             */
            Finished finished = new Finished("signature");
            byte[] finishedBytes = rWHelper.serializeObject(finished);
            messageObject = new Message(MessageType.finished, finishedBytes);
            rWHelper.writeMessage(out, messageObject);
            System.out.println("Sent Finished");

        } catch (IOException ex) {
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
}
