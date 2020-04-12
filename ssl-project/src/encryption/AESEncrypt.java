package encryption;

import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

public class AESEncrypt extends Encrypt {

    private Cipher encrypt_cipher;
    private Cipher decrypt_cipher;
    private SecretKeySpec key;
    private String algorithm = "AES";
    private int keyLength = 16;

    public AESEncrypt() {
        createKey();
        createCipher();
    }

    public AESEncrypt(byte[] user_key) {

        createKey(user_key);
        createCipher();
    }

    protected void createKey() {
        byte[] bytes = new byte[16];
        try {
            SecureRandom.getInstanceStrong().nextBytes(bytes);
            key = new SecretKeySpec(bytes, algorithm);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * instantiates key object with user provided key
     *
     * @param user_key
     */
    protected void createKey(byte[] user_key) {
        if (user_key.length > keyLength) {
            user_key = super.fixLength(user_key, keyLength);
        }
        key = new SecretKeySpec(user_key, algorithm);
    }

    protected void createCipher() {
        encrypt_cipher = super.createEncryptCipher(key, algorithm);
        decrypt_cipher = super.createDecryptCipher(key, algorithm);
    }

    /**
     * encrypts a plaintext message using a predefined Cipher object
     *
     * @param str the plaintext message to be encrypted
     * @return the encrypted message
     */
    public String encrypt(String str) {
        return super.encrypt(str, encrypt_cipher);
    }

    /**
     * decrypts an encrypted message using a predefined Cipher object
     *
     * @param str AES encrypted message
     * @return the decrypted message
     */
    public String decrypt(String str) {
        return super.decrypt(str, decrypt_cipher);
    }

    public byte[] getKey() {
        return key.getEncoded();
    }
}
