package models.handshake;

import java.io.Serializable;
import java.util.Random;

/**
 *
 * @author Richard
 */
public class ClientKeyExchange implements Serializable {
    // change this parameter from a string to whatever the paramters

    private byte[] parameters;

    public ClientKeyExchange(byte[] parameters) {
        this.parameters = parameters;
    }

    public ClientKeyExchange() {

    }

    public byte[] getParameters() {
        return this.parameters;
    }

    public void setParameters(byte[] parameters) {
        this.parameters = parameters;
    }

    public byte[] generatePremasterSecret() {
        Random random = new Random();
        byte[] preMasterSecret = new byte[48];
        random.nextBytes(preMasterSecret);
        return preMasterSecret;
    }
}
