package by.epam.kooks.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class, customer Password Encryption Tool
 *
 * @author Eugene Kooks
 */
public class Encoder {
    private static final Logger log = LogManager.getLogger(Encoder.class);

    /**
     * Encrypts a string to MD5 format
     *
     * @param txt - Unencrypted password
     * @return Returns encrypted password
     */
    public static String toEncode(String txt) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(txt.getBytes());
            StringBuffer sb = new StringBuffer();
            for (byte anArray : array) {
                sb.append(Integer.toHexString((anArray & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            log.warn("Encoding tool doesn't work",e);
        }
        return null;
    }
}
