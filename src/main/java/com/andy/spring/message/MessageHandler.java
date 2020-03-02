package com.andy.spring.message;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.CollectionUtils;

/**
 * 信息处理器,通过消息code转化为消息内容
 *
 * @author 庞先海 2019-11-14
 */
public class MessageHandler {

    private static Log log = LogFactory.getLog(MessageHandler.class);

    private static List<String> beanNames = new ArrayList<String>();

    private static Map<Locale, Properties> propertiesCacheMap = new ConcurrentHashMap<>();

    public static void setBeanNames(String... beanNames) {
        for (String beanName : beanNames) {
            setBeanName(beanName);
        }
    }

    public static String getMessage(int code, Object... args) {
        return getMessage(code, Locale.getDefault(), args);
    }

    private static void setBeanName(String beanName) {
        if (StringUtils.isNotBlank(beanName)) {
            beanNames.add(StringUtils.trim(beanName));
        }
    }

    private static String getMessage(int code, Locale locale, Object... args) {
        Properties properties = loadProperties(locale);
        String messageFormat = properties.getProperty(String.valueOf(code));
        if (StringUtils.isEmpty(messageFormat)) {
            return messageFormat;
        }
        return MessageFormat.format(messageFormat, args);
    }

    /**
     * 从该方法的逻辑来看 getMessage后在加入的beanName就读不到内容了 所以setBeanNames应当在系统初始化时设置
     */
    private static Properties loadProperties(Locale locale) {
        Properties properties = propertiesCacheMap.get(locale);
        if (properties == null) {
            properties = new Properties();
            List<String> fileNames = calculateFileNamesForLocale(locale);
            for (String fileName : fileNames) {
                properties.putAll(loadProperties(fileName + ".properties"));
            }
            if (properties.size() > 0) {
                propertiesCacheMap.put(locale, properties);
            }
        }
        return properties;
    }

    private static Properties loadProperties(String fileName) {
        Properties properties = new Properties();
        try {
            InputStream in = MessageHandler.class.getClassLoader().getResourceAsStream(fileName);
            if (in != null) {
                properties.load(new InputStreamReader(in));
            }
        } catch (IOException e) {
            log.error(fileName + " not found");
        }
        return properties;
    }

    private static List<String> calculateFileNamesForLocale(Locale locale) {
        List<String> fileNames = new ArrayList<>();
        if (! CollectionUtils.isEmpty(beanNames)) {
            beanNames.forEach(beanName -> fileNames.addAll(calculateFileNamesForLocale(beanName, locale)));
        }
        return fileNames;
    }

    private static List<String> calculateFileNamesForLocale(String beanName, Locale locale) {
        List<String> result = new ArrayList<>(3);
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String variant = locale.getVariant();
        StringBuilder temp = new StringBuilder(beanName);

        temp.append('-');
        if (StringUtils.isNotBlank(language)) {
            temp.append(language);
            result.add(0, temp.toString());
        }
        temp.append('-');
        if (StringUtils.isNotBlank(country)) {
            temp.append(country);
            result.add(0, temp.toString());
        }
        boolean hansLan = StringUtils.isNotBlank(country) || StringUtils.isNotBlank(language);
        if (StringUtils.isNotBlank(variant) && hansLan) {
            temp.append('-').append(variant);
            result.add(0, temp.toString());
        }
        return result;
    }
}
