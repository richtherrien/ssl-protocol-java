package server;
import hello.ServerHello;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Richard
 */
public class ServerRunnable implements Runnable {
    private final Socket socket;
    public ServerRunnable(Socket socket){
        System.out.println("Creating a runnable thread of the Server");
        this.socket = socket;
    }
    
    @Override
    public void run() {
        try {
            DataInputStream in = new DataInputStream(
                    new BufferedInputStream(socket.getInputStream()));
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            
            // Phase 1
            ServerHello sHello = new ServerHello(in, out);
            sHello.init();
            
            
            
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
