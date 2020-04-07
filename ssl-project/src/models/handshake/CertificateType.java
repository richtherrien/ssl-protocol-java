package models.handshake;

/**
 *
 * @author Richard
 */
public enum CertificateType {
    rsa_signature_only,
    dss_signature_only,
    rsa_diffiehellman,
    dss_diffiehellman,
    rsa_ephemeral_diffiehellman,
    dss_ephemeral_diffiehellman,
    fortezza,
}
