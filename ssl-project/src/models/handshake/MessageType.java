package models.handshake;

/**
 *
 * @author Richard
 */
public enum MessageType {
    hello_request,
    client_hello,
    server_hello,
    certificate,
    server_key_exchange,
    certificate_request,
    server_done,
    certificate_verify,
    client_key_exchange,
    finished
}
