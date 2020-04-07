package client;

import models.handshake.CertificateRequest;
import models.handshake.MessageType;
import models.handshake.CertificateVerify;
import models.handshake.Message;
import models.handshake.KeyExchange;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.handshake.*;
import utils.ReadWriteHelper;
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

            //// PHASE 1 not implemented
            messageObject = rWHelper.readMessage(in);
            /// PHASE 2
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
                        System.out.println("Recieved Server Certificate");
                        break;
                    case 5:
                        // certificate_request
                        this.certificateRequest = (CertificateRequest) rWHelper.deserialize(messageObject.getContent());
                        System.out.println("CertificateRequest. Type: " + certificateRequest.getCertificateType() + ", Certificate Authorities: " + certificateRequest.getCertificateAuthorities().toString());
                        break;
                    case 6:
                        // server_done 
                        System.out.println("Phase2 Server Done");
                        serverDone = true;
                        break;
                    default:
                }
            }

            //// PHASE 3 
            /// Certificate
            //get cert from the path
            Path path = FileSystems.getDefault().getPath(CLIENT_CERT);
            String fileLocation = path.toAbsolutePath().toString();
            System.out.println("FileLocation: " + fileLocation);
            X509Certificate serverCert = certManager.getCertificateFromFile(fileLocation);
            byte[] serverCertBytes = certManager.getEncodedCertificate(serverCert);
            messageObject = new Message(MessageType.certificate, serverCertBytes);
            // certificate send
            rWHelper.writeMessage(out, messageObject);
            System.out.println("Sent Server Client-Certificate");

            /**
             * Add the parameters to the client key exchange appropriately
             * modify the model KeyExchange to have the proper parameters
             *
             */
            KeyExchange keyExchange = new KeyExchange("signature", "parmeters");
            byte[] keyExchangeBytes = rWHelper.serializeObject(keyExchange);
            messageObject = new Message(MessageType.client_key_exchange, keyExchangeBytes);
            rWHelper.writeMessage(out, messageObject);
            System.out.println("Sent Client Key Exchange");

            /**
             * Add the parameters to the certificate verify modify the model
             * CertificateVerify to have the proper parameters
             *
             */
            CertificateVerify certificateVerify = new CertificateVerify("signature");
            byte[] certificateVerifyBytes = rWHelper.serializeObject(certificateVerify);
            messageObject = new Message(MessageType.certificate_verify, certificateVerifyBytes);
            rWHelper.writeMessage(out, messageObject);
            System.out.println("Sent Certificate Verify");

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

            // recieve the change cipher spec
            int changeCipherSpec = in.read();
            messageObject = rWHelper.readMessage(in);
            int typeInt = messageObject.getMessageType().ordinal();

            if (typeInt == 9) {
                Finished clientFinished = (Finished) rWHelper.deserialize(messageObject.getContent());
                System.out.println("Recieved Finished");
            }

            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
