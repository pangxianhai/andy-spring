package com.andy.spring.util;

import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

/**
 * 随机数工具
 *
 * @author 庞先海 2019-11-14
 */
public class RandomUtil {

    private static String[] RAND_CODES = new String[] {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "A", "B", "C",
                                                       "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P",
                                                       "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "a", "b", "c",
                                                       "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p",
                                                       "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

    private static String[] CODES = new String[] {"1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E",
                                                  "F", "G", "H", "I", "J", "K", "L", "M", "N", "P", "Q", "R", "S", "T",
                                                  "U", "V", "W", "X", "Y", "Z"};

    public static String getRandCode(int len, boolean isNumber) {
        Random random = new SecureRandom();
        StringBuilder codeBuilder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            if (isNumber) {
                codeBuilder.append(RAND_CODES[random.nextInt(10)]);
            } else {
                codeBuilder.append(RAND_CODES[random.nextInt(RAND_CODES.length)]);
            }
        }
        return codeBuilder.toString();
    }

    public static String getUuid() {
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replace("-", "");
        return uuid;
    }

    public static String getCode(int len) {
        Random random = new SecureRandom();
        StringBuilder codeBuilder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            codeBuilder.append(CODES[random.nextInt(CODES.length)]);
        }
        return codeBuilder.toString();
    }

    public static int getRandom(int bound) {
        Random random = new SecureRandom();
        return random.nextInt(bound);
    }
}
