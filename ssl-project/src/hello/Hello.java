package hello;

import java.io.*;
import java.security.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.handshake.Message;
import models.handshake.MessageType;
import utils.ReadWriteHelper;

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

    public Hello(DataInputStream in, DataOutputStream out, ArrayList<String> keyExchange, ArrayList<String> cipherAlg, ArrayList<String> macAlg) {
        this.in = in;
        this.out = out;
        this.keyExchange = keyExchange;
        this.cipherAlg = cipherAlg;
        this.macAlg = macAlg;
        this.SSLVersion = "SSLv3";
    }

    protected void setRandom(byte[] random) {

    }

    protected void setPreferredKeyExchange(String keyExchange) {
        this.preferredKeyExchange = keyExchange;
    }

    protected void setPreferredCipherAlg(String cipherAlg) {
        this.preferredCipherAlg = cipherAlg;
    }

    protected void setPreferredMACAlg(String macAlg) {
        this.preferredMACAlg = macAlg;
    }

    public String getSSLVersion() {
        return SSLVersion;
    }

    public String getPreferredKeyExchange() {
        return preferredKeyExchange;
    }

    public String getPreferredCipherAlg() {
        return preferredCipherAlg;
    }

    public String getPreferredMACAlg() {
        return preferredMACAlg;
    }

    protected String byteToString64(byte[] input) {
        String encoded = Base64.getEncoder().encodeToString(input);
        return encoded;
    }

    protected byte[] string64ToByte(String input) {
        byte[] decoded = Base64.getDecoder().decode(input);
        return decoded;
    }

    protected byte[] helloStringToByte(String message) {
        return message.getBytes(Charset.forName("UTF-8"));
    }

    protected String helloByteToString(byte[] message) {
        return new String(message, StandardCharsets.UTF_8);
    }

    protected byte[] concatByteArr(byte[] firstArr, byte[] secondArr) {
        byte[] result = new byte[firstArr.length + secondArr.length];
        System.arraycopy(firstArr, 0, result, 0, firstArr.length);
        System.arraycopy(secondArr, 0, result, firstArr.length, secondArr.length);
        return result;
    }

    protected byte[] generateRandom() {

        // initialize byte array as an empty array
        byte[] random = new byte[0];

        try {
            // Get the 32 bit TimeStamp
            int unixTimeStamp = (int) (System.currentTimeMillis() / 1000);
            byte[] TimeStamp = ByteBuffer.allocate(4).order(ByteOrder.nativeOrder()).putInt(unixTimeStamp).array();

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

    protected void sendMsgHSProtcol(MessageType messageType, byte[] parameters) {

        Message messageObject = new Message(messageType, parameters);
        ReadWriteHelper rWHelper = new ReadWriteHelper();
        rWHelper.writeMessage(out, messageObject);
    }

    protected String receiveMsgHSProtcol() {

        ReadWriteHelper rWHelper = new ReadWriteHelper();
        Message messageObject = rWHelper.readMessage(in);
        int typeInt = messageObject.getMessageType().ordinal();

        return helloByteToString(messageObject.getContent());

    }
}
