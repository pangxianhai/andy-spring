package com.andy.spring.base;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 基础DO定义
 *
 * @author 庞先海 2019-11-14
 */
@Getter
@Setter
@ToString
public class BaseDO implements Serializable {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 创建者
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新者
     */
    private String updatedBy;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
}
