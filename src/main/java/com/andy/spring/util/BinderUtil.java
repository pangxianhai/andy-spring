package com.andy.spring.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.PropertySourcesPlaceholdersResolver;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;

/**
 * spring Environment 绑定jave bean工具
 *
 * @author 庞先海 2019-11-17
 */
public class BinderUtil {

    public static <T> T bind(Environment environment, Class<T> clazz) {
        if (environment == null) {
            return null;
        }
        if (! (environment instanceof StandardEnvironment)) {
            return null;
        }
        StandardEnvironment standardEnvironment = (StandardEnvironment)environment;
        ConfigurationProperties configurationProperties = AnnotationUtils.findAnnotation(clazz,
            ConfigurationProperties.class);
        if (configurationProperties == null) {
            return null;
        }
        Binder binder = new Binder(getConfigurationPropertySources(standardEnvironment),
            getPropertySourcesPlaceholdersResolver(standardEnvironment));
        BindResult<T> bindResult = binder.bind(configurationProperties.prefix(), clazz);
        return bindResult.get();
    }


    private static Iterable<ConfigurationPropertySource> getConfigurationPropertySources(
        StandardEnvironment environment) {
        return ConfigurationPropertySources.from(environment.getPropertySources());
    }

    private static PropertySourcesPlaceholdersResolver getPropertySourcesPlaceholdersResolver(
        StandardEnvironment environment) {
        return new PropertySourcesPlaceholdersResolver(environment.getPropertySources());
    }
}
