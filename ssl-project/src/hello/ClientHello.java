/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hello;

import java.io.*;
import java.util.*;
import models.handshake.MessageType;

/**
 *
 * @author Rami
 */
public class ClientHello extends Hello{
    
    private final String sessionID;
    private byte[] myRandom;
    private byte[] randomFromServer = new byte[0];
    
    public ClientHello(DataInputStream in, DataOutputStream out, ArrayList<String> keyExchange, ArrayList<String> cipherAlg, ArrayList<String> macAlg){
        super(in, out, keyExchange, cipherAlg, macAlg);
        this.sessionID = "1";
    }
    
    @Override
    protected void setRandom(byte[] random){
        this.myRandom = random;
    }
    
    private void setRandomFromServer(byte[] random){
        this.randomFromServer = random;
    }
    
    public byte[] getRandom(){
        return myRandom;
    }
    
    
    public byte[] getRandomFromServer(){
        return randomFromServer;
    }
    
    public void init(){
        
        // generate secure Random byte array to send to Server
        byte[] randomFromClient = super.generateRandom();
        
        // send Client_Hello Message to Server using handshake protocol
        super.sendMsgHSProtcol(MessageType.client_hello, makeClientHello(randomFromClient));
        
        // recieve Message from server
        String msg = super.receiveMsgHSProtcol();
        
        // parse the Message data
        parseMessage(msg);
    }
    
    private byte[] makeClientHello(byte[] randomFromClient){
        
        // create the Hello Message
        String clientHello = SSLVersion 
                             + ","
                             + super.byteToString64(randomFromClient)
                             + ","
                             + sessionID 
                             + ","
                             + listToString(keyExchange)
                             + "," 
                             + listToString(cipherAlg) 
                             + "," 
                             + listToString(macAlg);
        
        byte[] parameters = super.helloStringToByte(clientHello);
        return parameters;
    }
    
    private String listToString(ArrayList<String> list){
        
        String stringFormat = "";
        Iterator i = list.iterator();

        i.next();
        for (String s : list){
            if (i.hasNext()){
                stringFormat += s + ",";
                i.next();
            }else{
                stringFormat += s;
            }
        }
        stringFormat = "(" + stringFormat + ")";
        return stringFormat;
    }
    
    private void parseMessage(String message){
        
         // split the message into its individual elements
        String[] parameters = message.split(",");
        
        // Make sure the SSL Versions Match
        if (!parameters[0].equals(SSLVersion)){
            throw new java.lang.Error("SSL Version Not Supported!");
        }
        
        // Set the Random recieved from the client
        setRandomFromServer(super.string64ToByte(parameters[1]));
        
        // Throw error for different sessionID (Feature yet to be supported)
        if (!parameters[2].equals(sessionID)){
            throw new java.lang.Error("New Connection Not Supported!");
        }
        
        // set the Preferred Cipher Suite that was decided on
        setPreferredKeyExchange(parameters[3]);
        setPreferredCipherAlg(parameters[4]);
        setPreferredMACAlg(parameters[5]);
    }
    
}
