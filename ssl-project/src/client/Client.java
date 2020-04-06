package client;

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
public class Client {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 5000;

    public Client() {
    }

    public void main() {
        try {
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            System.out.println("Connected to Server");
            DataInputStream in = new DataInputStream(
                    new BufferedInputStream(socket.getInputStream()));
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            // get the byte for the message type
            //writeMessage(in, out);

            // PHASE 2
            boolean serverDone = false;
            while (!serverDone) {
                // read message from the server
                ReadWriteHelper rWHelper = new ReadWriteHelper();
                MessageObject messageObject = rWHelper.readMessage(in);
                int typeInt = messageObject.getMessageType().ordinal();
                switch (typeInt) {
                    case 0:
                        // hello_request
                        break;
                    case 2:
                        // server_hello
                        break;
                    case 3:
                        // certificate
                        break;
                    case 4:
                        // server_key_exchange
                        break;
                    case 5:
                        // certificate_request
                        break;
                    case 6:
                        // server_done 
                        break;
                    case 7:
                        // certificate_verify
                        break;
                    case 8:
                        // client_key_exchange
                        break;
                    case 9:
                        // finished
                        break;
                    default:
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*public void writeMessage(DataInputStream in, DataOutputStream out){
        try {
            byte type = (byte) MessageObject.Type.client_hello.ordinal();
            out.write(type);
            // get the bytes from the content
            byte[] content = ("Here is a string version").getBytes();
            // determine the length of the conent
            int length = content.length;
            // convert the length to 4 bytes
            byte[] lengthByte = ByteBuffer.allocate(4).putInt(length).array();
            // write the content length in bytes
            out.write(lengthByte);
            // write the content bytes
            out.write(content);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public MessageObject readMessage(DataInputStream in, DataOutputStream out){
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
