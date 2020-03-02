package com.andy.spring.mybatis.paginator;

import java.util.List;
import org.apache.ibatis.mapping.ParameterMapping;

/**
 * mybatis数据
 *
 * @author 庞先海 2019-11-17
 */
public class DialectInfo {

    private Object parameterObject;

    private List<ParameterMapping> parameterMappings;

    private String pageSql;

    private String countSql;

    public Object getParameterObject() {
        return parameterObject;
    }

    public void setParameterObject(Object parameterObject) {
        this.parameterObject = parameterObject;
    }

    public List<ParameterMapping> getParameterMappings() {
        return parameterMappings;
    }

    public void setParameterMappings(List<ParameterMapping> parameterMappings) {
        this.parameterMappings = parameterMappings;
    }

    public String getPageSql() {
        return pageSql;
    }

    public void setPageSql(String pageSql) {
        this.pageSql = pageSql;
    }

    public String getCountSql() {
        return countSql;
    }

    public void setCountSql(String countSql) {
        this.countSql = countSql;
    }
}
