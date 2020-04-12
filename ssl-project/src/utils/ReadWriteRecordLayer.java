package utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.recordprotocol.ContentType;
import models.recordprotocol.MessageRecordLayer;

/**
 *
 * @author Richard Used for reading the input and writing to Streams for reacord
 * layer protocol
 */
public class ReadWriteRecordLayer {

    public void writeApplicationBytes(DataOutputStream out, byte[] applicationContent) {
        int maxFragmentLength = 16384;
        // check length of application content
        // if greater than 16384 bytes then crate new fragment
        int contentLength = applicationContent.length;
        if (contentLength <= maxFragmentLength) {
            MessageRecordLayer messageObject = new MessageRecordLayer(ContentType.application_data, applicationContent);
            writeMessage(out, messageObject);
            return;
        }

        double numberOfFragments = (double) contentLength / (double) maxFragmentLength;
        for (int i = 0; i < numberOfFragments; i++) {
            int offset = maxFragmentLength * i;
            byte[] fragmentContent = Arrays.copyOfRange(applicationContent, offset, maxFragmentLength);

            MessageRecordLayer messageObject = new MessageRecordLayer(ContentType.application_data, fragmentContent);
            writeMessage(out, messageObject);
        }
    }

    // write message to the output stream with the class variables
    private void writeMessage(DataOutputStream out, MessageRecordLayer messageObject) {
        try {
            byte contentType = messageObject.getContentTypeByte();
            byte majorVersion = messageObject.getMajorVersion();
            byte minorVerison = messageObject.getMinorVersion();
            byte[] length = messageObject.getLength();
            byte[] content = messageObject.getContent();

            out.write(contentType);
            out.write(majorVersion);
            out.write(minorVerison);
            out.write(length);
            // write the content bytes if not empty
            if (messageObject.getLengthShort() != 0) {
                out.write(content);
            }
            ContentType contentType2 = messageObject.getContentType();
            System.out.println("Write Type: " + messageObject.getContentType() + ", " + messageObject.getLengthShort() + ", " + contentType2);
        } catch (IOException ex) {
            Logger.getLogger(ReadWriteRecordLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // read message from input stream and set the class variables
    public MessageRecordLayer readMessage(DataInputStream in) {
        MessageRecordLayer messageObject;
        try {
            // read the message type and convert to the enum
            byte typeByte = in.readByte();
            ContentType contentyType = ContentType.values()[typeByte];
            byte majorVersion = in.readByte();
            byte minorVersion = in.readByte();
            // get the length of the message
            byte lengthBytes[] = new byte[2];
            in.read(lengthBytes, 0, 2);
            // get the length as a short
            short lengthShort = ByteBuffer.wrap(lengthBytes).getShort();

            // set the content array to the byte length and read the values
            byte messageContent[] = new byte[lengthShort];
            if (lengthShort != 0) {
                in.read(messageContent, 0, lengthShort);
                messageObject = new MessageRecordLayer(contentyType, messageContent);
            } else {
                messageObject = new MessageRecordLayer(contentyType, null);
            }
            ContentType contentType2 = messageObject.getContentType();
            // set the class variables
            System.out.println("Read Type: " + messageObject.getContentType() + ", " + messageObject.getLengthShort() + ", " + contentType2);
            return messageObject;
        } catch (IOException ex) {
            Logger.getLogger(ReadWriteRecordLayer.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(ReadWriteRecordLayer.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(ReadWriteRecordLayer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
