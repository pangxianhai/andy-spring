package com.andy.spring.mybatis.paginator;

import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.RowBounds;
import org.springframework.util.CollectionUtils;

/**
 * 分页功能
 *
 * @author 庞先海 2019-11-17
 */
public class Dialect {

    public DialectInfo init(MappedStatement mappedStatement, Object parameterObject, Page page) {
        DialectInfo dialectInfo = new DialectInfo();
        BoundSql boundSql = mappedStatement.getBoundSql(parameterObject);
        dialectInfo.setParameterMappings(new ArrayList<>(boundSql.getParameterMappings()));
        String sql = boundSql.getSql().trim();
        if (sql.endsWith(";")) {
            sql = sql.substring(0, sql.length() - 1);
        }

        String pageSql = sql;
        if (! CollectionUtils.isEmpty(page.getOrders())) {
            pageSql = getSortString(sql, page.getOrders());
        }
        if (page.getLimit() != RowBounds.NO_ROW_LIMIT) {
            pageSql = this.getLimitString(pageSql, page.getOffset(), page.getLimit());
        }
        dialectInfo.setPageSql(pageSql);
        dialectInfo.setCountSql(this.getCountString(sql));
        return dialectInfo;
    }

    /**
     * 将sql变成分页sql语句
     */
    protected String getLimitString(String sql, int offset, int limit) {
        throw new UnsupportedOperationException("limit sql not supported");
    }

    /**
     * 将sql转换为总记录数SQL
     *
     * @param sql SQL语句
     * @return 总记录数的sql
     */
    protected String getCountString(String sql) {
        throw new UnsupportedOperationException("count sql not supported");
    }

    /**
     * 将sql转换为带排序的SQL
     *
     * @param sql SQL语句
     * @return 总记录数的sql
     */
    protected String getSortString(String sql, List<Order> orders) {
        if (orders == null || orders.isEmpty()) {
            return sql;
        }
        StringBuffer buffer;
        if (sql.contains(" order by")) {
            //原sql含order by认为原sql中含有排序
            buffer = new StringBuffer("select * from (").append(sql).append(") temp_order order by ");
        } else {
            buffer = new StringBuffer(sql + " order by ");
        }
        for (Order order : orders) {
            if (order != null) {
                buffer.append(order.toString()).append(", ");
            }

        }
        buffer.delete(buffer.length() - 2, buffer.length());
        return buffer.toString();
    }
}
