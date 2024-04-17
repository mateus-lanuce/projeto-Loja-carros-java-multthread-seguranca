package apps.Utils;

import java.security.SecureRandom;

public class SaltValue {

    SaltValue() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Generates a random number string.
     * @param length the length of the random string.
     * @return a random string.
     */
    public static String getSaltString(int length) {
        StringBuilder salt = new StringBuilder();
        SecureRandom rnd = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int random = rnd.nextInt(9);
            salt.append(random);
        }
        return salt.toString();
    }

}
