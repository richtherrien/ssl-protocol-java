package encryption;

import encryption.RSAEncrypt;
import org.junit.Assert;
import org.junit.Test;

public class RSAEncryptTest {

    @Test
    public void testGetPublicKey() {
        RSAEncrypt rsaOwner = new RSAEncrypt();
        RSAEncrypt rsaReceiver = new RSAEncrypt();
        rsaReceiver.setReceiverPublicKey(rsaOwner.getPublicKey());
        
        String message = "test";
        String encMessage = rsaReceiver.encrypt(message);
        String result = rsaOwner.decrypt(encMessage);
        Assert.assertEquals(message, result);
    }
    
    @Test
    public void testEncrypt() {
        RSAEncrypt instance = new RSAEncrypt();
        // Initializing iwth its own key just for testing, in practice use 
        // use another key.
        instance.setReceiverPublicKey(instance.getPublicKey());
        String message = "test";
        String result = instance.encrypt(message);
        Assert.assertFalse(message.equals(result));
    }
    
}
