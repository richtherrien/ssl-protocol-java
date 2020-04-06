/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hello;

import java.io.*;
import java.security.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rami
 */
public class Hello {
    
    protected final ArrayList<String> keyExchange;
    protected final ArrayList<String> cipherAlg;
    protected final ArrayList<String> macAlg;
    protected final String SSLVersion;
    protected final DataInputStream in;
    protected final DataOutputStream out;
    protected String preferredKeyExchange = "";
    protected String preferredCipherAlg = "";
    protected String preferredMACAlg = "";
    
    public Hello(DataInputStream in, DataOutputStream out){
        this.in          = in; 
        this.out         = out; 
        this.keyExchange = new ArrayList<>();
        this.cipherAlg   = new ArrayList<>();
        this.macAlg      = new ArrayList<>();
        this.SSLVersion  = "SSLv3";
    }
    
    
    protected void setRandom(byte[] random){
        
    }
    
    protected void setPreferredKeyExchange(String keyExchange){
        this.preferredKeyExchange = keyExchange;
    }
    
    protected void setPreferredCipherAlg(String cipherAlg){
        this.preferredCipherAlg = cipherAlg;
    }
    
    protected void setPreferredMACAlg(String macAlg){
        this.preferredMACAlg = macAlg;
    }
    
    public String getSSLVersion(){
        return SSLVersion;
    }
    
    public String getPreferredKeyExchange(){
       return preferredKeyExchange;
    }
    
    public String getPreferredCipherAlg(){
        return preferredCipherAlg;
    }
    
    public String getPreferredMACAlg(){
        return preferredMACAlg;
    }
    
    protected String byteToString64(byte[] input){
      String encoded = Base64.getEncoder().encodeToString(input);
      return encoded;
    }
    
    protected byte[] string64ToByte(String input){
        byte[] decoded = Base64.getDecoder().decode(input);
        return decoded;   
    }
    
    protected byte[] helloStringToByte(String message){
        return message.getBytes(Charset.forName("UTF-8"));
    }
    
    protected String helloByteToString (byte[] message){
        return new String(message, StandardCharsets.UTF_8);
    }
    
    protected byte[] concatByteArr(byte[] firstArr, byte[] secondArr){
        byte[] result = new byte[firstArr.length + secondArr.length];
        System.arraycopy(firstArr, 0, result, 0, firstArr.length);
        System.arraycopy(secondArr, 0, result, firstArr.length, secondArr.length); 
        return result;
    }
    
    protected byte[] generateRandom(){
        
        // initialize byte array as an empty array
        byte[] random = new byte[0];
        
        try {
            // Get the 32 bit TimeStamp
            int unixTimeStamp = (int) (System.currentTimeMillis() / 1000);
            byte[] TimeStamp  =ByteBuffer.allocate(4).order(ByteOrder.nativeOrder()).putInt(unixTimeStamp).array();
            
            // Create a secure RNG 
            SecureRandom secureRandomGenerator;
            secureRandomGenerator = SecureRandom.getInstance("SHA1PRNG");
            
            // Generate Random 28 bits 
            byte[] randomBytes = new byte[28];
            secureRandomGenerator.nextBytes(randomBytes);
            
            // concatenate TimeStamp and Ramdomly generated bits, set the Random
            random = concatByteArr(TimeStamp, randomBytes);
            setRandom(random);
            
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Hello.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return random;
    }
    
    protected void sendMsgHSProtcol(String type, byte[] parameters){
        try {
            
            // send the Message Type
            out.writeUTF(type);
            
            out.flush();
            
            // calculate the message length
            int count = parameters.length;
            
            // send the Message length
            out.writeInt(count);

            System.out.println(Arrays.toString(parameters));

            // send the Message (Parameters)
            for(int i=0;i<count;i++){
                out.writeByte(parameters[i]);
            }
            
        } catch (IOException ex) {
            Logger.getLogger(Hello.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    protected String receiveMsgHSProtcol(){
        
        // initialize return String
        String fullMessage = "";
        try {
            
            // declare variables
            int count;

            // read in Message Type
            String type = "";
            type = in.readUTF();

            // read in the Message Length
            count = in.readInt();
            System.out.println(count);

            // read in Message Parameters
            byte[] parameters = new byte[count];
            for(int i=0;i<count;i++){
                parameters[i]=in.readByte();
            }
            System.out.println(helloByteToString(parameters));
            fullMessage = helloByteToString(parameters);
            
        } catch (IOException ex) {
            Logger.getLogger(Hello.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // return the parameters
        return fullMessage;
    }
}
