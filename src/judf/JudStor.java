package judf;

import java.io.*;

class JudStor implements Serializable
{
    private byte[] digest;
    private boolean initialized;
    private boolean nullvalue;
    private int MODVAL = 256;

    public JudStor() {
        this.digest = new byte[100];
        this.initialized = false;
        this.nullvalue = false;
    }

    private void doAggDigest( byte[] digest2) {
        int o = 0;
        for (int i = 0; i < this.digest.length; ++i) {
             int t = (this.digest[i] & 0x7F) + ((this.digest[i] < 0) ? 128 : 0) + (digest2[i] & 0x7F) + ((digest2[i] < 0) ? 128 : 0) + o;
            o = t / 256;
            this.digest[i] = (byte)(t % 256);
        }
    }

    public byte[] getDigest() {
        return this.digest;
    }

    public void setDigest( byte[] digest) {
        this.setInitialized(true);
        this.setNullvalue(false);
        this.digest = digest;
    }

    public void aggDigest( byte[] digestAgg) {
        if (!this.isInitialized()) {
            this.setDigest(digestAgg);
        }
        else {
            this.doAggDigest(digestAgg);
        }
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    public void setInitialized( boolean initialized) {
        this.initialized = initialized;
    }

    public boolean isNullvalue() {
        return this.nullvalue;
    }

    public void setNullvalue( boolean nullvalue) {
        this.nullvalue = nullvalue;
        this.initialized = true;
    }
}
