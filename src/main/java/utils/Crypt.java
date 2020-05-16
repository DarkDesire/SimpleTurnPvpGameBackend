package utils;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * Created by darkDesire on 02.10.2015.
 */
public class Crypt {
    public static String calcSha512(String data) {
        return DigestUtils.sha512Hex(data);
    }
}
