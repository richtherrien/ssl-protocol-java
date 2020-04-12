package AuthenticationCode;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Richard following the lesson from the oracle docs
 * https://docs.oracle.com/javase/tutorial/security/apisign/index.html
 */
public class SignatureGenVerify {

    public SignatureGenVerify() {
    }

    public byte[] signContent(PrivateKey privateKey, byte[] contentToBeSigned) {
        byte[] signature = null;
        try {
            Signature dsa = Signature.getInstance("SHA1withRSA");
            // initalize
            dsa.initSign(privateKey);
            dsa.update(contentToBeSigned);

            signature = dsa.sign();

        } catch (Exception ex) {
            Logger.getLogger(Signature.class.getName()).log(Level.SEVERE, null, ex);
        }
        return signature;
    }

    public boolean verifySignature(PublicKey publicKey, byte[] contentToBeVerified, byte[] signature) {
        boolean verified = false;
        try {
            Signature sig = Signature.getInstance("SHA1withRSA");
            sig.initVerify(publicKey);
            sig.update(contentToBeVerified);
            verified = sig.verify(signature);

        } catch (Exception ex) {
            Logger.getLogger(Signature.class.getName()).log(Level.SEVERE, null, ex);
        }
        return verified;
    }
}
