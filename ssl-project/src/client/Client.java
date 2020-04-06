package client;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.CertificateRequest;
import models.Message;
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

            // PHASE 1 not implemented
            messageObject = rWHelper.readMessage(in);
            // PHASE 2
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
            socket.close();

        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
