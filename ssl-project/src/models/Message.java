package models;

import java.io.Serializable;

/**
 *
 * @author Richard
 */
public class Message implements Serializable {

    public enum MessageType {
        hello_request,
        client_hello,
        server_hello,
        certificate,
        server_key_exchange,
        certificate_request,
        server_done,
        certificate_verify,
        client_key_exchange,
        finished
    }
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
