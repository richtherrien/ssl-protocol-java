package models.recordprotocol;

/**
 *
 * @author Richard
 */
public enum AlertType {
    unexpected_message,
    bad_record_mac,
    decompression_failure,
    handshake_failure,
    illegal_parameter,
    
    no_certificate,
    bad_certificate,
    unsupported_certificate,
    certificate_revoked,
    certificate_expired,
    certificate_unknown
}
