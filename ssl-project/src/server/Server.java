package server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Richard
 */
public class Server {
    private static final int PORT = 5000;

    public Server(){
    }
    
    public void main() {
        System.out.println("Starting main of Server");
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            while(true){
                Socket socket = serverSocket.accept();
                System.out.println("Initiator connected with Server");
                Thread thread = new Thread(new ServerRunnable(socket));
                thread.start();
                thread.join();
            }
        } catch (IOException | InterruptedException  ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }   
}