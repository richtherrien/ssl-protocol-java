package encryption;

import encryption.DESEncrypt;
import org.junit.Assert;
import org.junit.Test;

public class DESEncryptTest {
    /**
     * Test of decrypt method for DESEncrypt object with no argument
     */
    @Test
    public void testDecrypt_0args() {
        DESEncrypt desEncrypt = new DESEncrypt();
        String message = "This is a test";
        String messageEnc, messageDec;
        messageEnc = desEncrypt.encrypt(message);
        messageDec = desEncrypt.decrypt(messageEnc);
        Assert.assertEquals(message, messageDec);
    }
    
    /**
     * Test of decrypt method for DESEncrypt object with byte key
     */
    /**
     * Test of decrypt method for DESEncrypt object with no argument
     */
    @Test
    public void testDecrypt_byteArr() {
        DESEncrypt desInit = new DESEncrypt();
        DESEncrypt desEncrypt = new DESEncrypt(desInit.getKey());
        String message = "This is a test";
        String messageEnc, messageDec;
        messageEnc = desEncrypt.encrypt(message);
        messageDec = desEncrypt.decrypt(messageEnc);
        Assert.assertEquals(message, messageDec);
    }
    
}
