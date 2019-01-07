package benchmark.common;

import java.security.SecureRandom;

public class RandomAsciiStringGenerator {

    public final static String AVAILABLE_SYMBOLS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private final SecureRandom secureRandom;

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
            final int randomIndex = this.secureRandom.nextInt(RandomAsciiStringGenerator.AVAILABLE_SYMBOLS.length());
            stringBuilder.append(RandomAsciiStringGenerator.AVAILABLE_SYMBOLS.charAt(randomIndex));
        }
        return stringBuilder.toString();
    }


    public int getPayloadOfUTF8String(final String string) {
        // NOTE: UTF-8 string has 1 byte per symbol
        return string.length();
    }


}
