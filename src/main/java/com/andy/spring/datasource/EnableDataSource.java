package com.andy.spring.datasource;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * 开启数据源注解
 *
 * @author 庞先海 2019-11-17
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import({DataSourceRegistrar.class})
public @interface EnableDataSource {

    /**
     * 是否开启默认事物
     *
     * @return 默认不开启
     */
    boolean transaction() default false;
}
