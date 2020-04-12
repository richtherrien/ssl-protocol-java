package AuthenticationCode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;

public class MAC {

    protected byte[] MAC(Key key, String msg, String algorithm) {
        Mac mac = null;
        if ("MD5".equals(algorithm) || "SHA-1".equals(algorithm)) {

            try {
                mac = Mac.getInstance(algorithm);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(MAC.class.getName()).log(Level.SEVERE, null, ex);
            }
        } //Default to MD5
        else {
            System.out.println("Defaulted to MD5");
            try {
                mac = Mac.getInstance("MD5");
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(MAC.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //Initializing the Mac
        try {
            mac.init(key);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(MAC.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Computing the Mac
        byte[] bytes = msg.getBytes();
        byte[] macResult = mac.doFinal(bytes);
        System.out.println(algorithm);
        System.out.println("Mac result:");
        System.out.println(new String(macResult));
        return macResult;
    }

    public static byte[] defaultMD5(byte[] msg) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] thedigest = md.digest(msg);
            return thedigest;
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(MD5.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static byte[] defaultSHA(byte[] msg) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] thedigest = md.digest(msg);
            return thedigest;
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(MD5.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static byte[] defaultHASH(byte[] msg, String alg) {
        try {
            MessageDigest md = MessageDigest.getInstance(alg);
            byte[] thedigest = md.digest(msg);
            return thedigest;
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(MD5.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static byte[] generateMAC(byte[] macWriteSecret, byte[] seqNum, byte[] length, byte[] fragment) {

        byte[] pad1 = new byte[48];
        byte[] pad2 = new byte[48];
        Arrays.fill(pad2, (byte) 0x36);
        Arrays.fill(pad1, (byte) 0x36);

        // create MAC
        byte[] master = defaultMD5(concat(macWriteSecret, pad2, defaultMD5(concat(macWriteSecret, pad1, seqNum, length, fragment))));
        System.out.println(master.length);
        return master;

    }

    public static byte[] generateMaster(byte[] preMaster, byte[] clientRandom, byte[] serverRandom) {

        try {
            // concatenating byte arrays
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write(defaultMD5(concat(preMaster, defaultSHA(concat("A".getBytes(), clientRandom, serverRandom)))));
            out.write(defaultMD5(concat(preMaster, defaultSHA(concat("BB".getBytes(), clientRandom, serverRandom)))));
            out.write(defaultMD5(concat(preMaster, defaultSHA(concat("CCC".getBytes(), clientRandom, serverRandom)))));

            // convert secret master to byte array
            byte[] master = out.toByteArray();
            System.out.println(master.length);
            return master;

        } catch (IOException ex) {
            Logger.getLogger(MAC.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static byte[] concat(byte[]... arr) {
        int length = 0;
        for (byte[] array : arr) {
            length += array.length;
        }
        byte[] result = new byte[length];
        int pos = 0;
        for (byte[] array : arr) {
            System.arraycopy(array, 0, result, pos, array.length);
            pos += array.length;
        }
        return result;
    }
}
