package utils;

import java.io.File;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Richard
 */
public class ServerClientKeys {

    private static final String CLIENT_PRIV = "certificates/client_private_key.der";
    private static final String SERVER_PRIV = "certificates/server_private_key.der";

    public PrivateKey getClientPrivateKey() throws InvalidKeySpecException {
        return getPrivateKey(CLIENT_PRIV);
    }

    public PrivateKey getServerPrivateKey() throws InvalidKeySpecException {
        return getPrivateKey(SERVER_PRIV);
    }

    private PrivateKey getPrivateKey(String fileLocation) throws InvalidKeySpecException {
        PKCS8EncodedKeySpec keySpec = null;
        KeyFactory keyFactory = null;
        try {
            File file = new File(fileLocation);
            byte[] privateKey = Files.readAllBytes(file.toPath());
            keySpec = new PKCS8EncodedKeySpec(privateKey);
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (Exception ex) {
            Logger.getLogger(ServerClientKeys.class.getName()).log(Level.SEVERE, null, ex);
        }
        return keyFactory.generatePrivate(keySpec);
    }
}
