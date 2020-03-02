package com.andy.spring.page;

import com.andy.spring.mybatis.paginator.Order;
import com.andy.spring.mybatis.paginator.Page;

/**
 * 分页工具
 *
 * @author 庞先海 2019-11-19
 */
public class PageUtil {

    private final static int DEFAULT_PAGE_SIZE = 10;

    public static Page transformToPage(Paginator paginator) {
        Page page = new Page();
        if (paginator.getCurrentPage() == null || paginator.getCurrentPage() <= 0) {
            paginator.setCurrentPage(1);
        }
        if (paginator.getPageSize() == null || paginator.getPageSize() <= 0) {
            paginator.setPageSize(DEFAULT_PAGE_SIZE);
        }
        return new Page(paginator.getCurrentPage(), paginator.getPageSize(),
            Order.formString(paginator.getOrderSegment()));
    }

    public static Paginator transformToPaginator(Page page) {
        Paginator paginator = new Paginator();
        paginator.setCurrentPage(page.getCurrentPage());
        paginator.setPageSize(page.getPageSize());
        paginator.setTotalPage(page.getTotalPage());
        paginator.setTotalRecord(page.getTotalRecord());
        return paginator;
    }
}
