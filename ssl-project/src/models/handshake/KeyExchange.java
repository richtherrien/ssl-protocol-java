package models.handshake;

import java.io.Serializable;

/**
 *
 * @author Richard
 */
public class KeyExchange implements Serializable {
   // change this parameter from a string to whatever the paramters
    private String parameters;
    private String signature;

    public KeyExchange( String parameters, String signature) {
        this.parameters = parameters;
        this.signature = signature;
    }

    public String getParameters() {
        return this.parameters;
    }

    public void setParameters(String certificateType) {
        this.parameters = parameters;
    }

    public String getSignature() {
        return this.signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
