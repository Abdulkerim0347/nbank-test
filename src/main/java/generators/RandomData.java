package generators;

import org.apache.commons.lang3.RandomStringUtils;

import java.math.BigDecimal;
import java.util.Random;

public class RandomData {
    private RandomData() {}

    public static String getUsername() {
        return RandomStringUtils.randomAlphabetic(10);
    }

    public static String getPassword() {
        return RandomStringUtils.randomAlphabetic(2).toUpperCase() +
                RandomStringUtils.randomAlphabetic(5).toLowerCase() +
                RandomStringUtils.randomNumeric(5) + "%!#";
    }

    public static String getName() {
        return RandomStringUtils.randomAlphabetic(8);
    }

    public static Random getRandom() {
        return new Random();
    }
}
