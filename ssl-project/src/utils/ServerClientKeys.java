/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
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
        PKCS8EncodedKeySpec spec = null;
        KeyFactory kf = null;
        try {
            File file = new File(CLIENT_PRIV);
            byte[] privateKey = Files.readAllBytes(file.toPath());
            spec = new PKCS8EncodedKeySpec(privateKey);
            kf = KeyFactory.getInstance("RSA");
        } catch (Exception ex) {
            Logger.getLogger(ServerClientKeys.class.getName()).log(Level.SEVERE, null, ex);
        }
        return kf.generatePrivate(spec);
    }

    public PrivateKey getServerPrivateKey() throws InvalidKeySpecException {
        PKCS8EncodedKeySpec keySpec = null;
        KeyFactory keyFactory = null;
        try {
            File file = new File(SERVER_PRIV);
            byte[] privateKey = Files.readAllBytes(file.toPath());
            keySpec = new PKCS8EncodedKeySpec(privateKey);
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (Exception ex) {
            Logger.getLogger(ServerClientKeys.class.getName()).log(Level.SEVERE, null, ex);
        }
        return keyFactory.generatePrivate(keySpec);
    }
}
