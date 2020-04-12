package models.handshake;

import java.io.Serializable;

/**
 *
 * @author Richard
 */
public class CertificateVerify implements Serializable {
    private byte[] signature;

    public CertificateVerify(byte[] signature) {
        this.signature = signature;
    }

    public byte[] getSignature() {
        return this.signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }
}
