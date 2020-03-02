package com.andy.spring.mybatis.paginator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.session.RowBounds;

/**
 * mybatis分页功能 分页实体含排序
 *
 * @author 庞先海 2019-11-17
 */
public class Page extends RowBounds implements Serializable {

    private Integer currentPage;

    private Integer pageSize;

    private Integer totalRecord;

    private Integer totalPage;

    protected boolean containsTotalCount;

    protected Boolean asyncTotalCount;

    protected List<Order> orders = new ArrayList<Order>();

    public Page() {
    }

    public Page(int currentPage, int pageSize) {
        this(currentPage, pageSize, null, true);
    }

    public Page(List<Order> orders) {
        this(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT, orders, true);
    }


    public Page(int currentPage, int pageSize, boolean containsTotalCount) {
        this(currentPage, pageSize, null, containsTotalCount);
    }

    public Page(int currentPage, int pageSize, List<Order> orders) {
        this(currentPage, pageSize, orders, true);
    }

    public Page(int currentPage, int pageSize, List<Order> orders, boolean containsTotalCount) {
        super((currentPage - 1) * pageSize, pageSize);
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.orders = orders;
        this.containsTotalCount = containsTotalCount;
    }

    public Page(RowBounds rowBounds) {
        this.currentPage = (rowBounds.getOffset() / rowBounds.getLimit()) + 1;
        this.pageSize = rowBounds.getLimit();
        this.containsTotalCount = true;
    }

    public Page(RowBounds rowBounds, List<Order> orders) {
        this.currentPage = (rowBounds.getOffset() / rowBounds.getLimit()) + 1;
        this.pageSize = rowBounds.getLimit();
        this.orders = orders;
        this.containsTotalCount = true;
    }

    private void refreshPage() {
        this.totalPage = this.totalRecord % this.pageSize == 0 ? this.totalRecord / this.pageSize
            : this.totalRecord / this.pageSize + 1;
        if (this.currentPage > this.totalPage) {
            this.currentPage = this.totalPage;
        }
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
        refreshPage();
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public Boolean getAsyncTotalCount() {
        return asyncTotalCount;
    }

    public void setAsyncTotalCount(Boolean asyncTotalCount) {
        this.asyncTotalCount = asyncTotalCount;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public boolean isContainsTotalCount() {
        return containsTotalCount;
    }

    public void setContainsTotalCount(boolean containsTotalCount) {
        this.containsTotalCount = containsTotalCount;
    }

    private static int computePageNumber(int page, int pageSize, int totalItems) {
        if (page <= 1) {
            return 1;
        }
        if (Integer.MAX_VALUE == page || page > computeLastPageNumber(totalItems, pageSize)) {
            return computeLastPageNumber(totalItems, pageSize);
        }
        return page;
    }

    private static int computeLastPageNumber(int totalItems, int pageSize) {
        if (pageSize <= 0) {
            return 1;
        }
        int result = totalItems % pageSize == 0 ? totalItems / pageSize : totalItems / pageSize + 1;
        if (result <= 1) {
            result = 1;
        }
        return result;
    }

}

