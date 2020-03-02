package com.andy.spring.page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.CollectionUtils;

/**
 * 分页结果
 *
 * @author 庞先海 2019-11-14
 */
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = - 4184817785706628377L;
    /**
     * 数据
     */
    private List<T> data;

    /**
     * 页码
     */
    private Paginator paginator;

    public PageResult(List<T> data, Paginator paginator) {
        this.data = data;
        this.paginator = paginator;
        if (CollectionUtils.isEmpty(this.data)) {
            this.data = new ArrayList<T>(0);
        }
    }

    public List<T> getData() {
        return this.data;
    }

    public Paginator getPaginator() {
        return this.paginator;
    }
}
