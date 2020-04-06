package server;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.Message;
import utils.ReadWriteHelper;
import utils.X509CertificateManager;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import models.CertificateRequest;

/**
 *
 * @author Richard
 */
public class ServerRunnable implements Runnable {

    private final Socket socket;
    private static final String SERVER_CERT = "certificates/server.cer";
    private final ArrayList<String> authorities = new ArrayList<>(Arrays.asList("any", "Project Client", "coe817-client", "coe817"));

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
            Message messageObject = new Message(Message.MessageType.server_hello, null);
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

            byte[] serverCertBytes = certManager.getEncodedCertificate(serverCert);// = rWHelper.serializeObject(serverCert);
            int serverCertLength = serverCertBytes.length;
            messageObject = new Message(Message.MessageType.certificate, serverCertBytes);
            // certificate send
            rWHelper.writeMessage(out, messageObject);
            System.out.println("Sent Client Certificate");

            // SENDING CERTIFICATE REQUEST
            // create certificate request 
            CertificateRequest certificateRequest = new CertificateRequest(CertificateRequest.CertificateType.rsa_signature_only, this.authorities);
            byte[] certificateRequestBytes = rWHelper.serializeObject(certificateRequest);
            messageObject = new Message(Message.MessageType.certificate_request, certificateRequestBytes);
            rWHelper.writeMessage(out, messageObject);
            System.out.println("Sent Client Certificate Request");

            // SENDING SERVER DONE
            // send server done message
            messageObject = new Message(Message.MessageType.server_done, null);
            rWHelper.writeMessage(out, messageObject);
            System.out.println("Sent Client Server Done");

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
