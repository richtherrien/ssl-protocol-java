/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hello;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.*;
import models.handshake.MessageType;


/**
 *
 * @author Rami
 */
public class ServerHello extends Hello{
    
    private String sessionID;
    private byte[] myRandom;
    private byte[] randomFromClient;
    
    public ServerHello(DataInputStream in, DataOutputStream out, ArrayList<String> keyExchange, ArrayList<String> cipherAlg, ArrayList<String> macAlg){
        super(in, out, keyExchange, cipherAlg, macAlg);
    }
    
    @Override
    protected void setRandom(byte[] random){
        this.myRandom = random;
    } 
    
    private void setRandomFromClient(byte[] random){
        this.randomFromClient = random;
    }
    
    public byte[] getRandom(){
        return myRandom;
    }
    
    public byte[] getRandomFromClient(){
        return randomFromClient;
    }
    
    public void init(){
        
        // generate secure Random byte array to send to Server
        byte[] randomFromServer = super.generateRandom();
        
        // recieve Message from client
        String msg = super.receiveMsgHSProtcol();
        
        // parse the Message data
        parseMessage(msg);
        
        // send Server_Hello Message to Client using handshake protocol
        super.sendMsgHSProtcol(MessageType.server_hello, makeServerHello(randomFromServer));
        
        
    }
    
    private  byte[] makeServerHello(byte[] randomFromServer){
        
        // create the Hello Message
        String serverHello = SSLVersion 
                             + "," 
                             + super.byteToString64(randomFromServer) 
                             + "," 
                             + sessionID 
                             + ","
                             + super.getPreferredKeyExchange()
                             + "," 
                             + super.getPreferredCipherAlg()
                             + "," 
                             + super.getPreferredMACAlg();   
                            
        byte[] parameters = super.helloStringToByte(serverHello);
        return parameters;
    }
    
    private void parseMessage(String message){
        
        // split the message into its individual elements
        String[] parameters = message.split(",");
        
        // Make sure the SSL Versions Match
        if (!parameters[0].equals(SSLVersion)){
            throw new java.lang.Error("SSL Version Not Supported!");
        }
        
        // Set the Random recieved from the client
        setRandomFromClient(super.string64ToByte(parameters[1]));
        
        // Throw error for different sessionID (Feature yet to be supported)
        if (parameters[2].equals("0")){
            throw new java.lang.Error("New Connection Not Supported!");
        }else{
            sessionID = parameters[2];
        }
        
        // Match algorithms from client cipher suite, to algorithms availible in Server cipher suite
        algorithmMatching(message);
    }

    private void algorithmMatching(String message) {
        
        // flags 
        int preferedKeyEx = -1;
        int preferedCipherAlg = -1;
        int preferedMAC = -1;
        
        // separate paratheses groups
        Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(message);
        
        // split Cipher suite Candidates into separate groups
        m.find(); String[] keyExchangeCandidates = m.group(1).split(",");
        m.find(); String[] cipherAlgCandidates = m.group(1).split(",");
        m.find(); String[] macCandidates = m.group(1).split(",");
        
        // preform them matching and set the perfered Cipher Suite Candidates
        for (String s : keyExchangeCandidates){
            if(keyExchange.contains(s)){
                preferedKeyEx = 0;
                setPreferredKeyExchange(s);
                break;
            }   
        }
        
        for (String s : cipherAlgCandidates){
            if(cipherAlg.contains(s)){
                preferedCipherAlg = 0;
                setPreferredCipherAlg(s);
                break;
            }   
        }
        
        for (String s : macCandidates){
            if(macAlg.contains(s)){
                preferedMAC = 0;
                setPreferredMACAlg(s);
                break;
            }   
        }
        
        // Throw an error if no matching algorithms are found
        if(preferedKeyEx < 0 || preferedCipherAlg < 0 || preferedMAC < 0){
            throw new java.lang.Error("Incompatible Cipher Suites");
        }
        
    }
    
}
