package com.andy.spring.kafka;

import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

/**
 * kafka加载
 *
 * @author 庞先海 2019-11-24
 */
public class EnableKafkaConfigurationSelector implements ImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
            annotationMetadata.getAnnotationAttributes(EnableKafka.class.getName()));
        if (null == attributes) {
            return new String[0];
        }
        boolean producer = attributes.getBoolean("producer");
        boolean consumer = attributes.getBoolean("consumer");
        List<String> classNameList = new ArrayList<>(2);
        if (producer) {
            classNameList.add(KafkaProducerAutoConfiguration.class.getName());
        }
        if (consumer) {
            classNameList.add(KafkaConsumerAutoConfiguration.class.getName());
        }
        return classNameList.toArray(new String[] {});
    }
}
