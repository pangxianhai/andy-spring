package com.andy.spring.money;


import org.apache.commons.lang3.StringUtils;

/**
 * money工具
 *
 * @author 庞先海 2019-11-14
 */
public class MoneyUtil {

    public static String centToDollar(Long cent, String defaultValue) {
        if (null == cent) {
            return defaultValue;
        }
        try {
            Money money = new Money(cent);
            return money.toString();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static String centToDollar(Long cent) {
        return MoneyUtil.centToDollar(cent, "0.00");
    }

    public static long dollarToCent(String dollar, long defaultValue) {
        try {
            if (StringUtils.isEmpty(dollar)) {
                return defaultValue;
            } else {
                Money money = new Money(dollar);
                return money.getCent();
            }
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static long dollarToCent(String dollar) {
        return MoneyUtil.dollarToCent(dollar, 0L);
    }
}
