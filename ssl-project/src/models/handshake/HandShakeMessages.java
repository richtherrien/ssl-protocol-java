package models.handshake;

import java.io.Serializable;

/**
 *
 * @author Richard
 */
public class HandShakeMessages implements Serializable {

    public Message clientHello;
    public Message serverHello;
    public Message serverCertificate;
    public Message certificateRequest;
    public Message serverHelloDone;
    public Message clientCertificate;
    public Message clientKeyExchange;
    public Message certificateVerify;

    public HandShakeMessages() {
    }

    public HandShakeMessages(Message clientHello, Message serverHello,
            Message serverCertificate, Message certificateRequest,
            Message serverHelloDone, Message clientCertificate,
            Message clientKeyExchange, Message certificateVerify) {
        this.clientHello = clientHello;
        this.serverHello = serverHello;
        this.serverCertificate = serverCertificate;
        this.certificateRequest = certificateRequest;
        this.serverHelloDone = serverHelloDone;
        this.clientCertificate = clientCertificate;
        this.clientKeyExchange = clientKeyExchange;
        this.certificateVerify = certificateVerify;
    }

    public Message getClientHello() {
        return clientHello;
    }

    public void setClientHello(Message clientHello) {
        this.clientHello = clientHello;
    }

    public Message getServerHello() {
        return serverHello;
    }

    public void setServerHello(Message serverHello) {
        this.serverHello = serverHello;
    }

    public Message getServerCertificate() {
        return serverCertificate;
    }

    public void setServerCertificate(Message serverCertificate) {
        this.serverCertificate = serverCertificate;
    }

    public Message getCertificateRequest() {
        return certificateRequest;
    }

    public void setCertificateRequest(Message certificateRequest) {
        this.certificateRequest = certificateRequest;
    }

    public Message getServerHelloDone() {
        return serverHelloDone;
    }

    public void setServerHelloDone(Message serverHelloDone) {
        this.serverHelloDone = serverHelloDone;
    }

    public Message getClientCertificate() {
        return clientCertificate;
    }

    public void setClientCertificate(Message clientCertificate) {
        this.clientCertificate = clientCertificate;
    }

    public Message getClientKeyExchange() {
        return clientKeyExchange;
    }

    public void setClientKeyExchange(Message clientKeyExchange) {
        this.clientKeyExchange = clientKeyExchange;
    }

    public Message getCertificateVerify() {
        return certificateVerify;
    }

    public void setCertificateVerify(Message certificateVerify) {
        this.certificateVerify = certificateVerify;
    }
}
