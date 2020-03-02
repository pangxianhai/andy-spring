package com.andy.spring.executor;


import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 线程池配置
 *
 * @author 庞先海 2019-11-17
 */
@ConfigurationProperties(prefix = "executor")
public class ExecutorProperties {

    /**
     * 线程池大小
     */
    private Integer corePoolSize;

    /**
     * 最大大小
     */
    private Integer maximumPoolSize;

    /**
     * 最大活跃时间 毫秒
     */
    private Long keepAliveTime;

    /**
     * 队列大小
     */
    private Integer queueSize;

    /**
     * 线程名
     */
    private String threadName;


    public Integer getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(Integer corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public Integer getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(Integer maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public Long getKeepAliveTime() {
        return keepAliveTime;
    }

    public void setKeepAliveTime(Long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public Integer getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(Integer queueSize) {
        this.queueSize = queueSize;
    }
}
