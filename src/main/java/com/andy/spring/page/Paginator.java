package com.andy.spring.page;

import java.io.Serializable;

/**
 * 分页page定义
 *
 * @author 庞先海 2019-11-14
 */
public class Paginator implements Serializable {

    private static final long serialVersionUID = 7189578563832338583L;
    /**
     * 当前页
     */
    private Integer currentPage;

    /**
     * 页大小 每页多少条记录
     */
    private Integer pageSize;

    /**
     * 总记录数
     */
    private Integer totalRecord;

    /**
     * 总页数
     */
    private Integer totalPage;

    /**
     * 排序规则
     */
    private String orderSegment;

    public Paginator() {
        this.currentPage = 1;
        this.pageSize = Integer.MAX_VALUE;
    }

    public Paginator(int currentPage, int pageSize) {
        this(currentPage, pageSize, null);
    }

    public Paginator(int currentPage, int pageSize, String orderSegment) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.orderSegment = orderSegment;
    }

    public void nextPage() {
        if (null == currentPage) {
            currentPage = 1;
        } else {
            currentPage++;
        }
    }

    public boolean hasNextPage() {
        if (null == currentPage || null == totalPage) {
            return false;
        }
        return currentPage < totalPage;
    }

    public int getFrom() {
        if (null == this.currentPage || this.currentPage <= 0) {
            return 0;
        } else {
            return (this.currentPage - 1) * this.pageSize;
        }
    }


    public Paginator getPaginator() {
        return this;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(Integer totalRecord) {
        this.totalRecord = totalRecord;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public String getOrderSegment() {
        return orderSegment;
    }

    public void setOrderSegment(String orderSegment) {
        this.orderSegment = orderSegment;
    }
}
