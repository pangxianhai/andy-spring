package com.andy.spring.kafka;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * 是否开启kafka
 *
 * @author 庞先海 2019-11-24
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(EnableKafkaConfigurationSelector.class)
public @interface EnableKafka {

    /**
     * 开启生产者
     */
    boolean producer() default true;

    /**
     * 开启消费者
     */
    boolean consumer() default true;
}
