package models.recordprotocol;

import models.handshake.*;
import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 *
 * @author Richard
 */
public class MessageRecordLayer implements Serializable {

    // SSL V3 major version is 3 and minor version os 0
    private byte majorVersion = (byte) 3;
    private byte minorVersion = (byte) 0;
    private byte contentType;

    private byte[] content;

    public MessageRecordLayer() {
    }

    public MessageRecordLayer(ContentType contentType, byte[] content) {
        this.contentType = (byte) contentType.ordinal();
        this.content = content;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = (byte) contentType.ordinal();
    }

    public MessageType getContentType() {
        return MessageType.values()[this.contentType];
    }

    public byte getContentTypeByte() {
        return this.contentType;
    }

    // returns the byte length 
    public byte[] getLength() {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putShort((short) this.content.length);
        return buffer.array();
    }
    
    public short getLengthShort() {
        return (short) this.content.length;
    }

    public byte[] getContent() {
        return this.content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public byte getMajorVersion() {
        return this.majorVersion;
    }
    
    public byte getMinorVersion() {
        return this.minorVersion;
    }
}
