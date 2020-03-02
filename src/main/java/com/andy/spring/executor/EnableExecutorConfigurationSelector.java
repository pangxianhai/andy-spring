package com.andy.spring.executor;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 线程池加载
 *
 * @author 庞先海 2019-11-17
 */
public class EnableExecutorConfigurationSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        return new String[] {ExecutorAutoConfiguration.class.getName()};
    }
}
