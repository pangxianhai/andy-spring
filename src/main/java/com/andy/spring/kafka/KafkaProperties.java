package com.andy.spring.kafka;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * kafka配置
 *
 * @author 庞先海 2019-11-24
 */
@Data
@ConfigurationProperties(prefix = "kafka")
public class KafkaProperties {

    /**
     * 生产者配置
     */
    private ProducerProperties producer;

    /**
     * 消费者配置
     */
    private ConsumerProperties consumer;

    @Data
    public static class ProducerProperties {

        /**
         * 服务地址
         */
        private String servers;
    }

    @Data
    public static class ConsumerProperties {

        /**
         * 服务地址
         */
        private String servers;

        /**
         * 分组ID
         */
        private String groupId;

        /**
         * session超时时间 毫秒
         */
        private Integer sessionTimeout = 30000;

        /**
         * 自动提交频率 毫秒
         */
        private Integer autoCommitInterval = 1000;

        /**
         * 是否需要自动提交
         */
        private Boolean enableAutoCommit = true;
    }


}
