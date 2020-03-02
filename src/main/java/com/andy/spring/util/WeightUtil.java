package com.andy.spring.util;

import java.math.BigDecimal;
import org.apache.commons.lang3.StringUtils;

/**
 * 重量工具
 *
 * @author 庞先海 2019-11-14
 */

public class WeightUtil {

    /**
     * 克转换为千克
     *
     * @param gram 克
     * @return 千克的字符串
     * @author 庞先海 2017-08-21
     */
    public static String convertGramToKilograms(Long gram) {
        if (gram == null) {
            return null;
        }
        BigDecimal bigDecimal = new BigDecimal(gram);
        return bigDecimal.movePointLeft(3).stripTrailingZeros().toPlainString();
    }

    /**
     * 千克转换为克
     *
     * @param kilograms 千克的字符串
     * @return 克
     * @author 庞先海 2017-08-21
     */
    public static Long convertKilogramsToGram(String kilograms) {
        if (StringUtils.isEmpty(kilograms)) {
            return null;
        }
        BigDecimal bigDecimal = new BigDecimal(kilograms);
        return bigDecimal.movePointRight(3).longValue();
    }
}
