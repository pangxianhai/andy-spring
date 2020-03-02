package com.andy.spring.mybatis.paginator;

import com.andy.spring.executor.ExecutorUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * mybatis分页拦截器
 *
 * @author 庞先海 2019-11-17
 */
@Intercepts({
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class,
                                                                ResultHandler.class})})
public class PageInterceptor implements Interceptor {

    private static Logger logger = LoggerFactory.getLogger(PageInterceptor.class);

    private final ExecutorService executorService;

    private final Dialect dialect;

    public PageInterceptor(Dialect dialect) {
        this.dialect = dialect;
        this.executorService = ExecutorUtil.creatPoolExecutor("page-executor", 2, 4, 60000L, 1000);
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Executor executor = (Executor)invocation.getTarget();
        Object[] queryArgs = invocation.getArgs();
        MappedStatement ms = (MappedStatement)queryArgs[0];
        Object parameter = queryArgs[1];
        RowBounds rowBounds = (RowBounds)queryArgs[2];
        Page page = this.buildPage(rowBounds);
        if (null == page) {
            //没有page参数不分页
            return invocation.proceed();
        }
        DialectInfo dialectInfo = this.dialect.init(ms, parameter, page);
        dialectInfo.setParameterObject(parameter);

        BoundSql boundSql = ms.getBoundSql(parameter);
        queryArgs[0] = this.copyFromNewSql(ms, boundSql, dialectInfo.getPageSql(), dialectInfo.getParameterMappings(),
            dialectInfo.getParameterObject());
        queryArgs[1] = dialectInfo.getParameterObject();
        queryArgs[2] = new RowBounds(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
        if (null != executorService) {
            Future<Object> future = executorService.submit(invocation::proceed);
            if (page.isContainsTotalCount()) {
                Future<Integer> countFuture = executorService.submit(
                    () -> getCount(ms, boundSql, parameter, dialectInfo, executor));
                page.setTotalRecord(countFuture.get());
            }
            return future.get();
        } else {
            Integer totalRecord = this.getCount(ms, boundSql, parameter, dialectInfo, executor);
            page.setTotalRecord(totalRecord);
            return invocation.proceed();
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

    private Integer getCount(MappedStatement ms, BoundSql boundSql, Object parameter, DialectInfo dialectInfo,
        Executor executor) throws SQLException {
        Integer count;
        Cache cache = ms.getCache();
        if (cache != null && ms.isUseCache()) {
            BoundSql boundSqlCount = copyFromBoundSql(ms, boundSql, dialectInfo.getCountSql(),
                boundSql.getParameterMappings(), boundSql.getParameterObject());
            CacheKey cacheKey = executor.createCacheKey(ms, parameter, new Page(), boundSqlCount);
            count = (Integer)cache.getObject(cacheKey);
            if (count == null) {
                count = this.getCount(dialectInfo, ms, parameter, boundSql);
                cache.putObject(cacheKey, count);
            }
        } else {
            count = this.getCount(dialectInfo, ms, parameter, boundSql);
        }
        return count;
    }

    private MappedStatement copyFromNewSql(MappedStatement ms, BoundSql boundSql, String sql,
        List<ParameterMapping> parameterMappings, Object parameter) {
        BoundSql newBoundSql = this.copyFromBoundSql(ms, boundSql, sql, parameterMappings, parameter);
        return this.copyFromMappedStatement(ms, new BoundSqlSqlSource(newBoundSql));
    }

    private BoundSql copyFromBoundSql(MappedStatement ms, BoundSql boundSql, String sql,
        List<ParameterMapping> parameterMappings, Object parameter) {
        BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), sql, parameterMappings, parameter);
        for (ParameterMapping mapping : boundSql.getParameterMappings()) {
            String prop = mapping.getProperty();
            if (boundSql.hasAdditionalParameter(prop)) {
                newBoundSql.setAdditionalParameter(prop, boundSql.getAdditionalParameter(prop));
            }
        }
        return newBoundSql;
    }

    private MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource,
            ms.getSqlCommandType());

        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
            StringBuilder keyProperties = new StringBuilder();
            for (String keyProperty : ms.getKeyProperties()) {
                keyProperties.append(keyProperty).append(",");
            }
            keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
            builder.keyProperty(keyProperties.toString());
        }

        //setStatementTimeout()
        builder.timeout(ms.getTimeout());

        //setStatementResultMap()
        builder.parameterMap(ms.getParameterMap());

        //setStatementResultMap()
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());

        //setStatementCache()
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());

        return builder.build();
    }

    private int getCount(DialectInfo dialectInfo, MappedStatement mappedStatement, Object parameterObject,
        BoundSql boundSql) throws SQLException {
        String countSql = dialectInfo.getCountSql();
        logger.debug("Total count SQL [" + countSql + "]");
        logger.debug("Total count Parameters: " + parameterObject);

        Connection connection = null;
        PreparedStatement countStmt = null;
        ResultSet rs = null;
        try {
            connection = mappedStatement.getConfiguration().getEnvironment().getDataSource().getConnection();
            countStmt = connection.prepareStatement(countSql);
            DefaultParameterHandler handler = new DefaultParameterHandler(mappedStatement, parameterObject, boundSql);
            handler.setParameters(countStmt);
            rs = countStmt.executeQuery();
            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }
            logger.debug("Total count: " + count);
            return count;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } finally {
                try {
                    if (countStmt != null) {
                        countStmt.close();
                    }
                } finally {
                    if (connection != null && ! connection.isClosed()) {
                        connection.close();
                    }
                }
            }
        }
    }

    private Page buildPage(RowBounds rowBounds) {
        if (rowBounds instanceof Page) {
            return (Page)rowBounds;
        } else {
            return null;
        }
    }

    public static class BoundSqlSqlSource implements SqlSource {

        BoundSql boundSql;

        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        @Override
        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }
}
