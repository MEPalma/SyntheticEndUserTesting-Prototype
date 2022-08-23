package backend.utils;

import net.bytebuddy.utility.RandomString;
import org.apache.commons.codec.digest.DigestUtils;

import java.security.InvalidParameterException;

public class Hashing {
    public static final int PASSWD_SALT_LENGTH = 8;

    public static class HashResult {
        public final String hash;
        public final String salt;

        public HashResult(String hash, String salt) {
            this.hash = hash;
            this.salt = salt;
        }
    }

    public static HashResult hashPasswd(String passwd) throws InvalidParameterException {
        if (passwd == null || passwd.isEmpty())
            throw new InvalidParameterException("Empty password");
        String passwdSalt = RandomString.make(Hashing.PASSWD_SALT_LENGTH);
        String passwdHash = Hashing.hashOf(passwd, passwdSalt);
        return new HashResult(passwdHash, passwdSalt);
    }

    public static String hashOf(String passwd, String passwdSalt) {
        return DigestUtils.sha256Hex(passwd + passwdSalt);
    }

    public static boolean isPasswd(String passwd, String passwdSalt, String passwdHash) {
        return Hashing.hashOf(passwd, passwdSalt).equals(passwdHash);
    }
}
