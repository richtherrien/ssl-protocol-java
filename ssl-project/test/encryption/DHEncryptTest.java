/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encryption;

import org.junit.Test;
import org.junit.Assert;

public class DHEncryptTest {
    
    @Test
    public void testGetSecret() {
        DHEncrypt alice = new DHEncrypt();
        DHEncrypt bob = new DHEncrypt(alice.getPublicKey());
        alice.phaseOne(bob.getPublicKey());
        Assert.assertArrayEquals(alice.getSecret(), bob.getSecret());
    }
    
    @Test
    public void testDHEncrypt() {
        DHEncrypt alice = new DHEncrypt();
        DHEncrypt bob = new DHEncrypt(alice.getPublicKey());
        alice.phaseOne(bob.getPublicKey());
        
        AESEncrypt aliceEncrypt = new AESEncrypt(alice.getSecret());
        AESEncrypt bobEncrypt = new AESEncrypt(bob.getSecret());
        
        String message = "test";
        String encMessage = aliceEncrypt.encrypt(message);
        String decMessage = bobEncrypt.decrypt(encMessage);
        
        Assert.assertTrue(message.equals(decMessage));
                
    }
}
