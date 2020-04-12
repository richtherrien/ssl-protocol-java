/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
public class SHA {
     protected byte[] SHA(Key key, String msg) {  
        Mac mac = null;
        try {
            //Creating MAC
            mac = Mac.getInstance("SHA-1");
            //Initialize Mac Object
            mac.init(key);
        } catch (NoSuchAlgorithmException | InvalidKeyException ex) {
            Logger.getLogger(SHA.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Computing the Mac
        byte[] bytes = msg.getBytes();      
        byte[] macResult = mac.doFinal(bytes);
        System.out.println("Mac result:");
        System.out.println(new String(macResult));
        
        return macResult;
    }
}
