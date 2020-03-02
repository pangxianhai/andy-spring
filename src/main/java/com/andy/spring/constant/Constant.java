package com.andy.spring.constant;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

/**
 * 基本常量定义
 *
 * @author 庞先海 2019-11-14
 */
public class Constant {

    public static Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public static DateTimeFormatter DEFAULT_PATTERN = new DateTimeFormatterBuilder().appendPattern(
        "yyyy-MM-dd").parseDefaulting(ChronoField.NANO_OF_DAY, 0).toFormatter();
}
