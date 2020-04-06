package utils;

import models.Message;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Richard Used for reading the input and writing to Streams using the
 * Message Model
 */
public class ReadWriteHelper {

    // write message to the output stream with the class variables
    public void writeMessage(DataOutputStream out, Message messageObject) {
        try {
            byte typeByte = (byte) messageObject.getMessageType().ordinal();
            out.write(typeByte);
            // get the bytes from the content
            byte[] contentBytes = messageObject.getContent();
            // determine the length of the conent
            int lengthInt = messageObject.getLength();
            // convert the length to 4 bytes
            byte[] lengthByte = ByteBuffer.allocate(4).putInt(lengthInt).array();
            // write the content length in bytes
            out.write(lengthByte);
            // write the content bytes
            if (contentBytes != null) {
                out.write(contentBytes);
            }
            System.out.println("Write Type: " + messageObject.getMessageType() + ", " + messageObject.getLength() + ", " + messageObject.getContent());
        } catch (IOException ex) {
            Logger.getLogger(Message.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // read message from input stream and set the class variables
    public Message readMessage(DataInputStream in) {
        Message messageObject;
        try {
            // read the message type and convert to the enum
            byte typeByte = in.readByte();
            Message.MessageType messageType = Message.MessageType.values()[typeByte];
            // get the length of the content 
            byte lengthBytes[] = new byte[4];
            in.read(lengthBytes, 0, 4);
            int lengthInt = ByteBuffer.wrap(lengthBytes).getInt();
            // set the content array to the byte length and read the values
            byte messageContent[] = new byte[lengthInt];
            if (lengthInt != 0) {
                in.read(messageContent, 0, lengthInt);
                messageObject = new Message(messageType, messageContent);
            } else {
                messageObject = new Message(messageType, null);
            }

            // set the class variables
            System.out.println("Read Type: " + messageObject.getMessageType() + ", " + messageObject.getLength() + ", " + messageObject.getContent());
            return messageObject;
        } catch (IOException ex) {
            Logger.getLogger(Message.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    // serialize the object and convert to byte array
    public byte[] serializeObject(Object object) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(out);
            os.writeObject(object);
            return out.toByteArray();
        } catch (IOException ex) {
            Logger.getLogger(Message.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    // deserialize the object from byte array
    public Object deserialize(byte[] data) {
        try {
            ByteArrayInputStream bIn = new ByteArrayInputStream(data);
            ObjectInputStream oIn = new ObjectInputStream(bIn);
            return oIn.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Message.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
