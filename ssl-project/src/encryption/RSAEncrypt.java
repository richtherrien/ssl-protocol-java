package encryption;

import encryption.Encrypt;
import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class RSAEncrypt extends Encrypt {
    private Cipher signingCipher;
    private Cipher encryptionCipher;
    private Cipher decryptionCipher;
    private KeyPair myPair;

    /**
     * constructor used to create RSAEncrypt object
     */
    public RSAEncrypt() {
        createKey();
        createCipher();
    }

    /**
     * instantiates key object
     */
    protected void createKey() {
        KeyPairGenerator kpg = null;

        try {
            kpg = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        myPair = kpg.generateKeyPair();
    }

    /**
     * instantiates decryption and signing instances of the ciphers
     */
    protected void createCipher() {
        decryptionCipher = super.createDecryptCipher(myPair.getPrivate(), "RSA");
        signingCipher = super.createEncryptCipher(myPair.getPrivate(), "RSA");
        encryptionCipher = null;
    }

    /**
     * encrypts a plaintext message using a predefined Cipher object instantiated with the private key
     * @param str the plaintext message to be encrypted
     * @return the encrypted message
     */
    public String signMessage(String str) { return super.encrypt(str, signingCipher); }

    /**
     * decrypts an encrypted message using a predefined Cipher object instantiated with the private key
     * @param str RSA encrypted message to be decrypted
     * @return the decrypted message
     */
    public String decrypt(String str) {
        return super.decrypt(str, decryptionCipher);
    }
    
    /**
     * encrypts a message using a cipher created from someone else's public key,
     * requires instantiation of encryptionCipher through setEncryptKey before 
     * use
     * @param str plaintext message to be encrypted
     * @return the encrypted message
     */
    public String encrypt (String str) {
        if (encryptionCipher == null) {
            throw new RuntimeException("Initialize Encryption Cipher using the Receiver's Key before attempting encryption.");
        }
        return super.encrypt(str, encryptionCipher);
    }

    public void setReceiverPublicKey (byte [] enc_key) {
        EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(enc_key);
        
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey pubkey = keyFactory.generatePublic(encodedKeySpec);
            encryptionCipher = super.createEncryptCipher(pubkey, "RSA");
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * gets the public key of the RSAEncrypt object
     * @return the public key of the RSAEncrypt object
     */
    public byte[] getPublicKey() {
        return myPair.getPublic().getEncoded();
    }
}
