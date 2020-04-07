package models.handshake;

import java.io.Serializable;

/**
 *
 * @author Richard
 */
public class Finished implements Serializable {
    // change this parameter from a string to whatever the hash requires
    private String hashValue;

    public Finished(String hashValue) {
        this.hashValue = hashValue;
    }

    public String getHashValue() {
        return this.hashValue;
    }

    public void setHashValue(String hashValue) {
        this.hashValue = hashValue;
    }
}
