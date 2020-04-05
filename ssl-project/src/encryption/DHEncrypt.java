package encryption;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;

public class DHEncrypt {
    private KeyPair myPair = null;
    private KeyAgreement agreement = null;
    private byte[] sharedSecret;
    
    public DHEncrypt() {
        createKey();
    }
    
    public DHEncrypt(byte[] receiverKey) {
        createKey(receiverKey);
    }
    
    protected void createKey() {
        KeyPairGenerator kpg = null;

        try {
            kpg = KeyPairGenerator.getInstance("DH");
            agreement = KeyAgreement.getInstance("DH");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        myPair = kpg.generateKeyPair();
        
        try {
            agreement.init(myPair.getPrivate());
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }
    
    protected void createKey(byte[] receiverKey) {
        try {
            KeyFactory kf = KeyFactory.getInstance("DH");
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(receiverKey);
            PublicKey recPubKey = kf.generatePublic(x509EncodedKeySpec);
            DHParameterSpec recKeySpec = ((DHPublicKey)recPubKey).getParams();
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("DH");
            kpg.initialize(recKeySpec);
            myPair = kpg.genKeyPair();
            agreement = KeyAgreement.getInstance("DH");
            agreement.init(myPair.getPrivate());
            
            // Phase One when object initialized with key
            agreement.doPhase(recPubKey, true);
            sharedSecret = agreement.generateSecret();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        
    }
    
    public void phaseOne(byte[] receiverKey) {
        try {
            KeyFactory kf = KeyFactory.getInstance("DH");
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(receiverKey);
            PublicKey recPubKey = kf.generatePublic(x509KeySpec);
            agreement.doPhase(recPubKey, true);
            sharedSecret = agreement.generateSecret();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }
    
    public byte[] getSecret(){
        return sharedSecret;
    }
    
    public byte[] getPublicKey() {
        return myPair.getPublic().getEncoded();
    }
}
