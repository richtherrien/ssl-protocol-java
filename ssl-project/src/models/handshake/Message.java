package models.handshake;

import java.io.Serializable;

/**
 *
 * @author Richard
 */
public class Message implements Serializable {

    private MessageType type;
    private int length;
    private byte[] content;

    public Message() {
    }

    public Message(MessageType messageType, byte[] content) {
        this.type = messageType;
        this.content = content;
        try {
            this.length = content.length;

        } catch (NullPointerException ex) {
            this.length = 0;

        }
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public byte[] getContent() {
        return this.content;
    }

    public MessageType getMessageType() {
        return this.type;
    }

    public void setMessageType(MessageType messageType) {
        this.type = messageType;
    }

    public int getLength() {
        return this.length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
