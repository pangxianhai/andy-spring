package com.andy.spring.configuration;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 动态配置自动更新selector
 *
 * @author 庞先海 2019-12-21
 */
public class DynamicConfigurationSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        return new String[] {DynamicConfigurationAutoConfiguration.class.getName()};
    }
}
