package encryption;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Arrays;

public abstract class Encrypt {
    abstract void createCipher();
    abstract void createKey();

    protected Cipher createEncryptCipher(Key key, String transformation) {
        Cipher encrypt_cipher = null;

        try {
            encrypt_cipher = Cipher.getInstance(transformation);
            encrypt_cipher.init(Cipher.ENCRYPT_MODE, key);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return encrypt_cipher;
    }

    protected Cipher createDecryptCipher(Key key, String transformation) {
        Cipher decrypt_cipher = null;

        try {
            decrypt_cipher = Cipher.getInstance(transformation);
            decrypt_cipher.init(Cipher.DECRYPT_MODE, key);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        return decrypt_cipher;
    }

    /**
     * encrypts a plaintext message using a predefined Cipher object
     * @param str the plaintext message to be encrypted
     * @return the encrypted message
     */
    protected String encrypt(String str, Cipher encrypt_cipher) {
        String encrypted_message = "";
        byte[] utf8 = new byte[0];

        try {
            utf8 = str.getBytes("UTF8");
            encrypted_message = Base64.getEncoder().encodeToString(encrypt_cipher.doFinal(utf8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        return encrypted_message;
    }

    /**
     * decrypts an encrypted message using a predefined Cipher object
     * @param str DES encrypted message
     * @return the decrypted message
     */
    protected String decrypt(String str, Cipher decrypt_cipher) {
        String decrypted_message = "";

        try {
            // Decode String using Base64 to get Byte
            byte[] dec = Base64.getDecoder().decode(str);

            // Finish Decryption of Bytes and Convert to String
            decrypted_message = new String(decrypt_cipher.doFinal(dec), "UTF8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return decrypted_message;
    }
    
    protected byte[] fixLength (byte[] key, int length) {
        return Arrays.copyOfRange(key, 0, length);
    }
}
