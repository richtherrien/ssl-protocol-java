package AuthenticationCode;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Mac;

/**
 *
 * @author Roman Makuch
 */
public class MD5 {

    protected byte[] MD5(Key key, String msg) {
        Mac mac = null;
        try {
            //Creating MAC
            mac = Mac.getInstance("MD5");
            //Initialize Mac Object
            mac.init(key);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(MD5.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(MD5.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Computing the Mac
        byte[] bytes = msg.getBytes();
        byte[] macResult = mac.doFinal(bytes);
        System.out.println("Mac result:");
        System.out.println(new String(macResult));

        return macResult;
    }
}
