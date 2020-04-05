package encryption;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

public class TripleDESEncrypt extends Encrypt {
    private Cipher encrypt_cipher;
    private Cipher decrypt_cipher;
    private SecretKey key;
    private int keyLength = 24;
    
    
    public TripleDESEncrypt() {
        createKey();
        createCipher();
    }
    
    public TripleDESEncrypt(byte[] user_key) {
        if (user_key.length > keyLength) {
            user_key = super.fixLength(user_key, keyLength);
        }
        createKey(user_key);
        createCipher();
    }
    
    protected void createKey() {
        try {
            // DESede is the name for 3DES
            key = KeyGenerator.getInstance("DESede").generateKey(); 
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    
     /**
     * instantiates key object with user provided key
     * @param user_key
     */
    protected void createKey(byte[] user_key) {
        SecretKeyFactory factory;

        try {
            factory = SecretKeyFactory.getInstance("DESede");
            key = factory.generateSecret(new DESedeKeySpec(user_key));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }
    
    protected void createCipher() { 
        encrypt_cipher = super.createEncryptCipher(key, "DESede");
        decrypt_cipher = super.createDecryptCipher(key, "DESede");
    }
    
    /**
     * encrypts a plaintext message using a predefined Cipher object
     * @param str the plaintext message to be encrypted
     * @return the encrypted message
     */
    public String encrypt(String str) {
        return super.encrypt(str, encrypt_cipher);
    }

    /**
     * decrypts an encrypted message using a predefined Cipher object
     * @param str DES encrypted message
     * @return the decrypted message
     */
    public String decrypt(String str) {
        return super.decrypt(str, decrypt_cipher);
    }

    /**
     * returns the key of the DESEncrypt object
     * @return the key of the DESEncrypt object
     */
    public byte[] getKey () {
        byte[] rawDesEdeKey = null;
        
        try {
            SecretKeyFactory desEdeFactory = SecretKeyFactory.getInstance("DESede");
            DESedeKeySpec desEdeSpec = (DESedeKeySpec) desEdeFactory.getKeySpec(key, javax.crypto.spec.DESedeKeySpec.class);
            rawDesEdeKey = desEdeSpec.getKey();
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        
        return rawDesEdeKey;
    }
}
