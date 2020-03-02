package com.andy.spring.util;

import com.andy.spring.constant.Constant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

/**
 * 日期格式化工具类
 *
 * @author 庞先海 2019-11-14
 */
public class DateUtil {

    private final static ZoneOffset UTC8 = ZoneOffset.of("+8");

    /**
     * 根据年月日生成时间
     *
     * @param year  年
     * @param month 月
     * @param day   日
     * @return 日期
     */
    public static LocalDateTime from(int year, int month, int day) {
        return LocalDateTime.of(year, month, day, 0, 0);
    }

    /**
     * 当前时间的上个月
     *
     * @return 上月日期
     */
    public static LocalDateTime lastMonth() {
        LocalDateTime now = LocalDateTime.now();
        return now.minusMonths(1);
    }

    /**
     * 上个月
     *
     * @return 上月日期
     */
    public static LocalDateTime lastMonth(LocalDateTime dateTime) {
        return dateTime.minusMonths(1);
    }

    /**
     * 月初第一天
     *
     * @param dateTime 日期
     * @return 第一天日期
     */
    public static LocalDateTime beginOfMonth(LocalDateTime dateTime) {
        LocalDate firstDay = LocalDate.of(dateTime.getYear(), dateTime.getMonth(), 1);
        return LocalDateTime.of(firstDay, LocalTime.MIN);
    }

    /**
     * 月末最后一天
     *
     * @param dateTime 日期
     * @return 最后一天日期
     * @author Gary.pu 2019-06-20
     */
    public static LocalDateTime endOfMonth(LocalDateTime dateTime) {
        LocalDate lastDay = dateTime.toLocalDate().with(TemporalAdjusters.lastDayOfMonth());
        return LocalDateTime.of(lastDay, LocalTime.MAX);
    }

    public static LocalDateTime beginOfDay(LocalDateTime dateTime) {
        return LocalDateTime.of(dateTime.toLocalDate(), LocalTime.MIN);
    }

    /**
     * 格式化时间
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) {
            return null;
        }
        DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern(pattern).parseDefaulting(
            ChronoField.NANO_OF_DAY, 0).toFormatter();
        return formatter.format(dateTime);
    }

    /**
     * 格式化时间
     */
    public static String format(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        LocalDateTime dateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return format(dateTime, pattern);
    }

    /**
     * 格式化时间
     */
    public static String format(Date date, DateTimeFormatter formatter) {
        if (date == null) {
            return null;
        }
        LocalDateTime dateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return formatter.format(dateTime);
    }

    /**
     * 格式化 当前时间
     */
    public static String format(String pattern) {
        return format(LocalDateTime.now(), pattern);
    }

    /**
     * 日期格式化
     *
     * @param date 格式化的日期
     */
    public static String format(LocalDateTime date) {
        return date.format(Constant.DEFAULT_PATTERN);
    }

    /**
     * 毫秒转date
     */
    public static Date millisecondsToDate(Long milliseconds) {
        if (milliseconds == null) {
            return null;
        }
        return new Date(milliseconds);
    }

    /**
     * 当前日期毫秒
     */
    public static long currentMilliseconds() {
        return System.currentTimeMillis();
    }

    public static long currentSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 字符串解析成日期
     *
     * @param dateText 日期字符串
     * @param pattern  日期格式
     */
    public static LocalDateTime parse(String dateText, String pattern) {
        if (StringUtils.isEmpty(dateText)) {
            return null;
        }
        DateTimeFormatter format = new DateTimeFormatterBuilder().appendPattern(pattern).parseDefaulting(
            ChronoField.NANO_OF_DAY, 0).toFormatter();
        return LocalDateTime.parse(dateText, format);
    }

    /**
     * 字符串解析成日期
     *
     * @param dateText 日期字符串
     * @param pattern  日期格式
     */
    public static Date parseDate(String dateText, String pattern) {
        if (StringUtils.isEmpty(dateText)) {
            return null;
        }
        org.joda.time.format.DateTimeFormatter formatter = DateTimeFormat.forPattern(pattern);
        DateTime dt = formatter.parseDateTime(dateText);
        return dt.toDate();
    }

    /**
     * 通过默认格式 字符串解析成日期
     *
     * @param dateText 日期字符串
     */
    public static LocalDateTime parse(String dateText) {
        return LocalDateTime.parse(dateText, Constant.DEFAULT_PATTERN);
    }

    /**
     * 解析日期字符串 并转化为时间戳
     */
    public static Long parseForMills(String dateText, String pattern) {
        LocalDateTime dateTime = parse(dateText, pattern);
        if (dateTime == null) {
            return null;
        }
        return dateTime.toInstant(UTC8).toEpochMilli();
    }

    /**
     * 解析日期字符串 并转化为时间戳
     */
    public static Long parseForMills(String dateText) {
        LocalDateTime dateTime = parse(dateText);
        return dateTime.toInstant(UTC8).toEpochMilli();
    }

    /**
     * dateTime转时间戳
     */
    public static Long parseForMills(LocalDateTime dateTime) {
        return dateTime.toInstant(UTC8).toEpochMilli();
    }
}
