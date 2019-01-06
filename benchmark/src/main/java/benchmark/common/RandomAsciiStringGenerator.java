package benchmark.common;

import java.security.SecureRandom;

public class RandomAsciiStringGenerator {

    private final String ASCII_SYMBOLS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private final SecureRandom secureRandom;


    // MARK: - Constructor
    public RandomAsciiStringGenerator() {
        this.secureRandom = new SecureRandom();
    }

    // MARK: - Public methods

    public String getRandomString(final int length) {
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

}
