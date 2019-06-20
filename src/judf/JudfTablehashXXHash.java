package judf;

import com.teradata.fnc.*;
import java.sql.*;
import java.nio.ByteBuffer;
import net.jpountz.xxhash.XXHash64;
import net.jpountz.xxhash.XXHashFactory;

public class JudfTablehashXXHash
{

    static XXHashFactory factory = XXHashFactory.fastestInstance();

    static XXHash64 hash64 = factory.hash64();
    static long seed1 = 6223301041919860527L;

    public static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(20);
        buffer.putLong(x);
        return buffer.array();
    }

    public static String byteArrayToHexString(byte[] bytes) {
        final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        char[] hexChars = new char[bytes.length * 2]; // Each byte has two hex characters (nibbles)
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF; // Cast bytes[j] to int, treating as unsigned value
            hexChars[j * 2] = hexArray[v >>> 4]; // Select hex character from upper nibble
            hexChars[j * 2 + 1] = hexArray[v & 0x0F]; // Select hex character from lower nibble
        }
        return new String(hexChars);
    }

    public static byte[]  toXXHash( String value) {
        return longToBytes(hash64.hash(value.getBytes(), 0, value.length(), seed1));
    }

    public static String tablehash_xxhash( Phase phase, Context[] context, String tin) throws SQLException {
        String returnValue = null;
        try {
            JudStor s1 = null;

            if (phase.getPhase() > Phase.AGR_INIT && phase.getPhase() < Phase.AGR_NODATA) {
                s1 = (JudStor)context[0].getObject(1);
            }
            switch (phase.getPhase()) {
                case Phase.AGR_INIT: {
                    s1 = new JudStor();
                }
                case Phase.AGR_DETAIL: {
                    if (tin == null) {
                        s1.setNullvalue(true);
                        break;
                    }
                    s1.aggDigest(toXXHash(tin));
                    break;
                }
                case Phase.AGR_COMBINE: {
                     JudStor s2 = (JudStor)context[0].getObject(2);
                    if (s1.isNullvalue() || s2.isNullvalue()) {
                        s1.setNullvalue(true);
                        s2.setNullvalue(true);
                        break;
                    }
                    s1.aggDigest(s2.getDigest());
                    break;
                }
                case Phase.AGR_FINAL: {
                    if (!s1.isInitialized()) {
                        return null;
                    }
                    if (s1.isNullvalue()) {
                        return null;
                    }
                    return byteArrayToHexString(s1.getDigest());
                }
                case Phase.AGR_NODATA: {
                    return null;
                }
                default: {
                    throw new SQLException("Invalid Phase", "38U05");
                }
            }
            context[0].setObject(1, (Object)s1);
        }
        catch (Exception ex) {

            throw new SQLException(ex.toString(), "38101");
        }
        return returnValue;
    }
}
