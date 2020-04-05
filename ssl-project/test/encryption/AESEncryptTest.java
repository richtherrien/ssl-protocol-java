package encryption;

import org.junit.Test;
import org.junit.Assert;

public class AESEncryptTest {
    
    @Test
    public void testCreateKey_byteArr() {
        System.out.println("createKey");
        byte[] user_key = "testtesttesttest".getBytes();
        AESEncrypt instance = new AESEncrypt(user_key);
        Assert.assertArrayEquals(user_key, instance.getKey());
    }

    @Test
    public void testDecrypt() {
        String expResult = "test";
        AESEncrypt instance = new AESEncrypt();
        String result = instance.encrypt(expResult);
        result = instance.decrypt(result);
        
        Assert.assertTrue(expResult.equals(result));
    }
    
    @Test
    public void testGetKey() {
        AESEncrypt instance = new AESEncrypt();
        AESEncrypt test = new AESEncrypt(instance.getKey());
        Assert.assertArrayEquals(instance.getKey(), test.getKey());
    }
}
