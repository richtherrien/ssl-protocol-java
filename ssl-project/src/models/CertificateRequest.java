package models;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Richard
 */
public class CertificateRequest implements Serializable{

    public enum CertificateType {
        rsa_signature_only,
        dss_signature_only,
        rsa_diffiehellman,
        dss_diffiehellman,
        rsa_ephemeral_diffiehellman,
        dss_ephemeral_diffiehellman,
        fortezza,
    }
    private CertificateType certificateType;
    private List<String> certificateAuthorities;

    public CertificateRequest(CertificateType certificateType, List<String> certificateAuthorities) {
        this.certificateType = certificateType;
        this.certificateAuthorities = certificateAuthorities;
    }

    public CertificateType getCertificateType() {
        return this.certificateType;
    }

    public void setCertificateType(CertificateType certificateType) {
        this.certificateType = certificateType;
    }

    public List<String> getCertificateAuthorities() {
        return this.certificateAuthorities;
    }

    public void setCertificateAuthorities(List<String> certificateAuthorities) {
        this.certificateAuthorities = certificateAuthorities;
    }
}
