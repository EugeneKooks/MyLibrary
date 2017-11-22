package by.epam.kooks.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class EncoderTest {
    private static final String TEST_PASSWORD = "1234";
    private static final String EXPECTED_HASHED_PASSWORD = "81dc9bdb52d04dc20036dbd8313ed055";

    @Test
    public void encodePasswordTest() throws Exception {
        String hashedPassword = Encoder.toEncode(TEST_PASSWORD);
        assertEquals(EXPECTED_HASHED_PASSWORD, hashedPassword);
    }
}
