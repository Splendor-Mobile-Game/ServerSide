package com.github.splendor_mobile_game.websocket.utils;

import java.security.SecureRandom;

/** This class generates a random string of specified length using a set of allowed characters. */
public class RandomString {
    /** The set of allowed characters to be used in generating the random string. */
    private static final String ALLOWED_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    /**
     * Generates a random string of specified length using the set of allowed characters.
     *
     * @param length the length of the random string to be generated
     * @return the generated random string
     */
    public static String generateRandomString(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(ALLOWED_CHARS.length());
            sb.append(ALLOWED_CHARS.charAt(index));
        }

        return sb.toString();
    }
}
