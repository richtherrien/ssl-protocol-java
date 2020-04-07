package models.handshake;

import java.io.Serializable;

/**
 *
 * @author Richard
 */
public class CertificateVerify implements Serializable {
    private String signature;

    public CertificateVerify(String signature) {
        this.signature = signature;
    }

    public String getSignature() {
        return this.signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
