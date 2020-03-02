package com.andy.spring.mybatis.handler;

import com.andy.spring.type.BaseType;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

/**
 * BaseType的myBaseTypeHandler
 *
 * @author 庞先海 2019-11-17
 */
public class MyBaseTypeHandler<T extends BaseType> extends BaseTypeHandler<T> {

    private List<T> values;


    public MyBaseTypeHandler(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        values = BaseType.getValues(type);
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getCode());
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int code = rs.getInt(columnName);
        if (rs.wasNull()) {
            return null;
        } else {
            return parse(code);
        }
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int code = rs.getInt(columnIndex);
        if (rs.wasNull()) {
            return null;
        } else {
            return parse(code);
        }
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int code = cs.getInt(columnIndex);
        if (cs.wasNull()) {
            return null;
        } else {
            return parse(code);
        }
    }

    private T parse(int code) {
        try {
            return BaseType.parse(values, code);
        } catch (RuntimeException e) {
            //todo 日志
            return null;
        }
    }
}
