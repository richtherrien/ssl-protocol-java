package encryption;


import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class DESEncrypt extends Encrypt {
    private Cipher encrypt_cipher;
    private Cipher decrypt_cipher;
    private SecretKey key;
    private int keyLength = 8;

    /**
     * constructor used to create DESEncrypt object
     */
    public DESEncrypt() {
        createKey();
        createCipher();
    }

    /**
     * constructor used to create DESEncrypt object with user provided key
     * @param user_key user provided key for DES encryption
     */
    public DESEncrypt(byte[] user_key) {
        if (user_key.length > keyLength) {
            user_key = super.fixLength(user_key, keyLength);
        }
        
        createKey(user_key);
        createCipher();
    }

    public DESEncrypt(String user_key) {
        try {
            byte[] utf8 = user_key.getBytes();
            SecretKeyFactory factory = null;
            factory = SecretKeyFactory.getInstance("DES");
            key = factory.generateSecret(new DESKeySpec(utf8));
            createCipher();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

    }

    /**
     * instantiates key object assuming no user key provided
     */
    protected void createKey() {
        try {
            key = KeyGenerator.getInstance("DES").generateKey();
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
            factory = SecretKeyFactory.getInstance("DES");
            key = factory.generateSecret(new DESKeySpec(user_key));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    /**
     * instantiates decryption and encryption instances of cipher
     */
    protected void createCipher() {
        encrypt_cipher = super.createEncryptCipher(key, "DES");
        decrypt_cipher = super.createDecryptCipher(key, "DES");
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
        byte[] rawDesKey = null;
        
        try {
            SecretKeyFactory desFactory = SecretKeyFactory.getInstance("DES");
            DESKeySpec desSpec = (DESKeySpec) desFactory.getKeySpec(key, javax.crypto.spec.DESKeySpec.class);
            rawDesKey = desSpec.getKey();
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        
        return rawDesKey;
    }
}