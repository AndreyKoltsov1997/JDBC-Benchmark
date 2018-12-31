package benchmark.common;

import java.security.SecureRandom;

public class RandomAsciiStringGenerator {

    private final String ASCII_SYMBOLS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    private final int stringLength;
    private final SecureRandom secureRandom;


    // MARK: - Constructor
    public RandomAsciiStringGenerator(final int stringLength) {
        this.stringLength = stringLength;
        this.secureRandom = new SecureRandom();
    }

    // MARK: - Public methods

    public String getRandomString() {
        StringBuilder stringBuilder = new StringBuilder(this.stringLength);
        for (int i = 0; i < this.stringLength; i++) {
            final int randomIndex = this.secureRandom.nextInt(this.ASCII_SYMBOLS.length());
            stringBuilder.append(this.ASCII_SYMBOLS.charAt(randomIndex));
        }
        return stringBuilder.toString();
    }

}
