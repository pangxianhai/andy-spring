package com.andy.spring.datasource;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 数据源配置
 *
 * @author 庞先海 2019-11-17
 */
@ConfigurationProperties(prefix = "spring.jdbc")
@Data
public class JdbcProperties {

    /**
     * datasource配置列表
     */
    private List<DataSourceProperties> dataSourceList;

    @Data
    public static class DataSourceProperties {

        /**
         * 数据库别名会记录到连接池名称,多数据源区分不同的db
         */
        private String dbAlias;
        /**
         * 数据库连接驱动名称
         */
        private String driverClassName;
        /**
         * 数据库连接地址
         */
        private String jdbcUrl;
        /**
         * 用户名
         */
        private String username;
        /**
         * 密码
         */
        private String password;
        /**
         * mybatis mapper扫描路劲
         */
        private String mapperScan;
        /**
         * mapper xml路径
         */
        private String mapperLocations;
        /**
         * 等待连接池分配连接的最大时长（毫秒）
         */
        private long connectionTimeout = 30000;
        /**
         * 连接将被测试活动的最大时间量
         */
        private long validationTimeout = 5000;
        /**
         * 一个连接idle状态的最大时长（毫秒），超时则被释放
         */
        private long idleTimeout = 600000;
        /**
         * 一个连接的生命时长（毫秒），超时而且没被使用则被释放
         */
        private long maxLifetime = 1800000;
        /**
         * 连接池中允许的最大连接数
         */
        private int maxPoolSize = 10;
        /**
         * 池中维护的最小空闲连接数
         */
        private int minimumIdle = 1;

    }
}