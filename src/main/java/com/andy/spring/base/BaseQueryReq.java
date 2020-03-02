package com.andy.spring.base;

import com.andy.spring.page.Paginator;
import java.io.Serializable;
import lombok.Data;

/**
 * 基础查询参数
 *
 * @author 庞先海 2020-01-30
 */
@Data
public class BaseQueryReq implements Serializable {

    private static int DEFAULT_PAGE_SIZE = 50;

    /**
     * 当前页
     */
    private Integer currentPage;

    /**
     * 查询开始
     */
    private Integer from;

    /**
     * 页大小
     */
    private Integer pageSize;

    /**
     * 排序规则
     */
    private String orderSegment;

    public static void setDefaultPageSize(int defaultPageSize) {
        DEFAULT_PAGE_SIZE = defaultPageSize;
    }

    public Paginator getPaginator() {
        if (null == this.pageSize || this.pageSize < 0) {
            this.pageSize = DEFAULT_PAGE_SIZE;
        }
        if (null != this.from && this.from >= 0) {
            this.currentPage = this.from / this.pageSize + 1;
        }
        if (null == this.currentPage || this.currentPage <= 0) {
            this.currentPage = 1;
        }
        return new Paginator(this.currentPage, this.pageSize, orderSegment);
    }
}
