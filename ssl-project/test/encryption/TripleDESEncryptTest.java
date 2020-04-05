package encryption;

import org.junit.Assert;
import org.junit.Test;

public class TripleDESEncryptTest {
    
    /**
     * Test of decrypt method for DESEncrypt object with no argument
     */
    @Test
    public void testDecrypt_0args() {
        TripleDESEncrypt desEncrypt = new TripleDESEncrypt();
        String message = "This is a test";
        String messageEnc, messageDec;
        messageEnc = desEncrypt.encrypt(message);
        messageDec = desEncrypt.decrypt(messageEnc);
        Assert.assertEquals(message, messageDec);
    }
    
    /**
     * Test of decrypt method for DESEncrypt object with byte key
     */
    @Test
    public void testDecrypt_byteArr() {
        TripleDESEncrypt desInit = new TripleDESEncrypt();
        TripleDESEncrypt desEncrypt = new TripleDESEncrypt(desInit.getKey());
        String message = "This is a test";
        String messageEnc, messageDec;
        messageEnc = desEncrypt.encrypt(message);
        messageDec = desEncrypt.decrypt(messageEnc);
        Assert.assertEquals(message, messageDec);
    }
    
}
