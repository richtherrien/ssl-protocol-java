package utils;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Richard
 */
public class X509CertificateManager {

    public X509CertificateManager() {
    }

    // return X509Certificate from fileName
    public X509Certificate getCertificateFromFile(String fileName) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(fileName);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(X509CertificateManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return getCertificate(inputStream);
    }

    // return X509Certificate from bytes
    public X509Certificate getCertificateFromBytes(byte[] certificateBytes) {
        InputStream inputStream = new ByteArrayInputStream(certificateBytes);
        return getCertificate(inputStream);
    }

    public X509Certificate getCertificate(InputStream inputStream) {
        try {
            CertificateFactory certFactory;
            certFactory = CertificateFactory.getInstance("X.509");
            X509Certificate certificate = (X509Certificate) certFactory.generateCertificate(inputStream);
            return certificate;
        } catch (CertificateException ex) {
            Logger.getLogger(X509CertificateManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public byte[] getEncodedCertificate(X509Certificate cert) {
        try {

            return cert.getEncoded();
        } catch (CertificateException ex) {
            Logger.getLogger(X509CertificateManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
