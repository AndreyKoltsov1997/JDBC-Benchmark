package benchmark.common;

import benchmark.Constants;

import java.security.SecureRandom;

public class RandomAsciiStringGenerator {

    private final static String ASCII_SYMBOLS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private final SecureRandom secureRandom;
    private final static int RANDOM_KEY_LENGTH = 10;


    // MARK: - Constructor
    public RandomAsciiStringGenerator() {
        this.secureRandom = new SecureRandom();
    }

    // MARK: - Public methods

    public String getRandomString(final int length) {
        final String emptyString = "";
        if (length == 0) {
            return emptyString;
        }
        StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            final int randomIndex = this.secureRandom.nextInt(this.ASCII_SYMBOLS.length());
            stringBuilder.append(this.ASCII_SYMBOLS.charAt(randomIndex));
        }
        return stringBuilder.toString();
    }


    public int getPayloadOfUTF8String(final String string) {
        // NOTE: UTF-8 string has 1 byte per symbol
        return string.length();
    }


    // NOTE: Key is a random string with fixed length
    public String getRandomKey() {
        return this.getRandomString(RandomAsciiStringGenerator.RANDOM_KEY_LENGTH);
    }

}
