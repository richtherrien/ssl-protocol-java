package project;

import java.io.Serializable;

/**
 *
 * @author Richard
 */
public class MessageObject implements Serializable{
    public enum Type {
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
    
    private Type type;
    private int length;
    private Object content;
    public  MessageObject(){
    }
    public  MessageObject(Type messageType, int length, Object content){
        this.type = messageType;
        this.content = content;
        this.length = length;
    }
    public void setContent(Object content){
        this.content = content;
    }
    public Object getContent(){
        return this.content;
    }
    public Type getMessageType(){
        return this.type;
    }
    public void setMessageType(Type messageType){
        this.type = messageType;
    }
    
    public int getLength(){
        return this.length;
    }
    public void setLength(int length){
        this.length = length;
    } 
}
