package server;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import project.MessageObject;
import project.ReadWriteHelper;

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
            ReadWriteHelper rWHelper = new ReadWriteHelper();
            // write to client
            // phase 1
            MessageObject messageObject = new MessageObject(MessageObject.Type.server_hello, 1, null);
            rWHelper.writeMessage(out, messageObject);
            
            // PHASE 2
            // certificate send
            messageObject = new MessageObject(MessageObject.Type.certificate,  1, null);
            rWHelper.writeMessage(out, messageObject);
            // server key exchange
            messageObject = new MessageObject(MessageObject.Type.server_key_exchange, 1, null);
            rWHelper.writeMessage(out, messageObject);
            // certificate request
            messageObject = new MessageObject(MessageObject.Type.certificate_request, 1, null);
            rWHelper.writeMessage(out, messageObject);
            
            // certificate request
            messageObject = new MessageObject(MessageObject.Type.server_done, 1, null);
            rWHelper.writeMessage(out, messageObject);
            
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
    
   /* public MessageObject readMessage(DataInputStream in, DataOutputStream out){
        try {
            // read the message type and convert to the enum
            byte typeByte = in.readByte();
            MessageObject.Type type = MessageObject.Type.values()[typeByte];
            // get the length of the content 
            byte length[] = new byte[4];
            in.read(length, 0, 4); 
            int lengthInt =  ByteBuffer.wrap(length).getInt();
            // set the content array to the byte length and read the values
            byte content[] = new byte[lengthInt];
            in.read(content, 0, lengthInt);
            System.out.println("Type: "+type+", Length: "+ lengthInt+", Content: " + new String(content));
            return new MessageObject(type, lengthInt, content);
        } catch (IOException ex) {
            Logger.getLogger(ServerRunnable.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }*/
}
