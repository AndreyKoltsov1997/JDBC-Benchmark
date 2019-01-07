package benchmark.common;

import org.junit.Test;

import static org.junit.Assert.*;


// NOTE: Expecting generator of random UTF-8 string that contains only latin letters and numbers, 1 byte per symbol.
public class RandomAsciiStringGeneratorTest {

    @Test
    public void getRandomString() {
        RandomAsciiStringGenerator randomAsciiStringGenerator = new RandomAsciiStringGenerator();
        final int requiredLength = 10;
        final String generatedString = randomAsciiStringGenerator.getRandomString(requiredLength);
        // NOTE: Expected string should contain only latin letters and numbers. (UTF-8 1-byte characters)
        final String containsOnlyLattersAndNumbersRexExp = "^[a-zA-Z0-9]*";
        boolean isStringContentCorrect = generatedString.matches(containsOnlyLattersAndNumbersRexExp);

        // NOTE: Expected a string contains only UTF-8 1-byte characters, thus 1 byte per character.
        final int generatedStringPayload = randomAsciiStringGenerator.getPayloadOfUTF8String(generatedString);
        boolean isStringPayloadCorrect = (generatedStringPayload == requiredLength);

        assertTrue(isStringContentCorrect && isStringPayloadCorrect);
    }

    // NOTE: Expecting string with 1 byte per character.
    @Test
    public void getPayloadOfUTF8String() {
        final int requiredPayload = 100;
        RandomAsciiStringGenerator randomAsciiStringGenerator = new RandomAsciiStringGenerator();

        final String stringContainingPayload = randomAsciiStringGenerator.getRandomString(requiredPayload);
        final int generatedStringPayload = randomAsciiStringGenerator.getPayloadOfUTF8String(stringContainingPayload);

        boolean isTestCorrect = (requiredPayload == generatedStringPayload);
        assertTrue(isTestCorrect);
    }
}