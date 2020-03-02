package com.andy.spring.mybatis.paginator;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * 分页功能 mysql分页SQL拼接
 *
 * @author 庞先海 2019-11-17
 */
public class MysqlDialect extends Dialect {

    @Override
    public String getLimitString(String sql, int offset, int limit) {
        return sql + " limit " + offset + ", " + limit;
    }

    @Override
    public String getCountString(String sql) {
        if (StringUtils.isEmpty(sql)) {
            return sql;
        }
        if (this.isSampleSql(sql)) {
            String[] sqlArr = sql.split("[\t| |\n][Ff][Rr][Oo][Mm][\t| |\n]");
            if (sqlArr.length <= 1) {
                return "select count(1) from (" + sql + ") tmp";
            }
            List<String> sqlList = new ArrayList<>();
            sqlList.add("select count(1)");
            for (int i = 1; i < sqlArr.length; ++ i) {
                sqlList.add(sqlArr[i]);
            }
            return StringUtils.join(sqlList, " from ");
        } else {
            return "select count(1) from (" + sql + ") tmp";
        }
    }

    private boolean isSampleSql(String sql) {
        String lowerSql = sql.toLowerCase();
        boolean includeGroup = lowerSql.contains("group by");
        boolean includeDistinct = lowerSql.contains("distinct");
        return ! (includeGroup || includeDistinct);
    }
}
