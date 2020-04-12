package models.handshake;

import java.io.Serializable;

/**
 *
 * @author Richard
 */
public class Finished implements Serializable {

    // change this parameter from a string to whatever the hash requires
    private byte[] hashValue;

    public Finished(byte[] hashValue) {
        this.hashValue = hashValue;
    }

    public byte[] getHashValue() {
        return this.hashValue;
    }

    public void setHashValue(byte[] hashValue) {
        this.hashValue = hashValue;
    }
}
