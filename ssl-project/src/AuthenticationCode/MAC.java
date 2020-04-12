
package AuthenticationCode;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;

public class MAC {
     protected byte[] MAC(Key key, String msg, String algorithm) {
        Mac mac = null;
        if ("MD5".equals(algorithm) || "SHA-1".equals(algorithm)){
         
            try {
                mac = Mac.getInstance(algorithm);
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(MAC.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //Default to MD5
        else{
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
}