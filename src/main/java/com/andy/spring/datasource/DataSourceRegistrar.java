package com.andy.spring.datasource;

import static org.springframework.beans.factory.support.BeanDefinitionBuilder.rootBeanDefinition;

import com.andy.spring.datasource.JdbcProperties.DataSourceProperties;
import com.andy.spring.mybatis.paginator.Dialect;
import com.andy.spring.mybatis.paginator.MysqlDialect;
import com.andy.spring.mybatis.paginator.PageInterceptor;
import com.andy.spring.util.BinderUtil;
import com.zaxxer.hikari.HikariDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.CollectionUtils;

/**
 * dataSource注册
 *
 * @author 庞先海 2019-11-17
 */
public class DataSourceRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware {

    private static Logger logger = LoggerFactory.getLogger(DataSourceRegistrar.class);

    private Environment environment;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata,
        BeanDefinitionRegistry beanDefinitionRegistry) {
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
            annotationMetadata.getAnnotationAttributes(EnableDataSource.class.getName()));
        if (null == attributes) {
            return;
        }
        JdbcProperties jdbcProperties = BinderUtil.bind(environment, JdbcProperties.class);

        if (null == jdbcProperties || CollectionUtils.isEmpty(jdbcProperties.getDataSourceList())) {
            logger.warn("未加载到jdbc配置");
            return;
        }
        for (DataSourceProperties dataSourceProperties : jdbcProperties.getDataSourceList()) {
            String dataSourceBean = this.registerDataSource(dataSourceProperties, beanDefinitionRegistry);
            String factoryBeanName = this.registerSessionFactory(dataSourceProperties, dataSourceBean,
                beanDefinitionRegistry);
            this.registerMapperScanner(dataSourceProperties, factoryBeanName, beanDefinitionRegistry);
            this.registerSessionTemplate(dataSourceProperties, factoryBeanName, beanDefinitionRegistry);
        }
    }

    private String registerDataSource(DataSourceProperties properties, BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder builder = rootBeanDefinition(HikariDataSource.class);
        builder.addPropertyValue("driverClassName", properties.getDriverClassName());
        builder.addPropertyValue("jdbcUrl", properties.getJdbcUrl());
        builder.addPropertyValue("username", properties.getUsername());
        builder.addPropertyValue("password", properties.getPassword());
        builder.addPropertyValue("poolName", properties.getDbAlias());
        builder.addPropertyValue("connectionTimeout", properties.getConnectionTimeout());
        builder.addPropertyValue("validationTimeout", properties.getValidationTimeout());
        builder.addPropertyValue("idleTimeout", properties.getIdleTimeout());
        builder.addPropertyValue("maxLifetime", properties.getMaxLifetime());
        builder.addPropertyValue("maximumPoolSize", properties.getMaxPoolSize());
        builder.addPropertyValue("minimumIdle", properties.getMinimumIdle());
        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        String beanName = properties.getDbAlias() + "DataSource";
        registry.registerBeanDefinition(beanName, beanDefinition);
        return beanName;
    }

    private String registerSessionFactory(DataSourceProperties properties, String dataSourceName,
        BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder builder = rootBeanDefinition(SqlSessionFactoryBean.class);
        builder.addPropertyReference("dataSource", dataSourceName);
        builder.addPropertyValue("mapperLocations", properties.getMapperLocations());

        Dialect dialect = new MysqlDialect();
        PageInterceptor pageInterceptor = new PageInterceptor(dialect);
        builder.addPropertyValue("plugins", pageInterceptor);

        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        String beanName = properties.getDbAlias() + "SessionFactory";
        registry.registerBeanDefinition(beanName, beanDefinition);
        return beanName;
    }

    private void registerMapperScanner(DataSourceProperties properties, String factoryBeanName,
        BeanDefinitionRegistry registry) {

        BeanDefinitionBuilder builder = rootBeanDefinition(MapperScannerConfigurer.class);
        builder.addPropertyValue("basePackage", properties.getMapperScan());
        builder.addPropertyValue("sqlSessionFactoryBeanName", factoryBeanName);
        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        String beanName = properties.getDbAlias() + "MapperScanner";
        registry.registerBeanDefinition(beanName, beanDefinition);
    }

    private void registerSessionTemplate(DataSourceProperties properties, String factoryBeanName,
        BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder builder = rootBeanDefinition(SqlSessionTemplate.class);
        builder.addConstructorArgReference(factoryBeanName);
        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        String beanName = properties.getDbAlias() + "SessionTemplate";
        registry.registerBeanDefinition(beanName, beanDefinition);
    }


    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
