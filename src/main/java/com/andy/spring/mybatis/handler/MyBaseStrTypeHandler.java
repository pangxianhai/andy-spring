package com.andy.spring.mybatis.handler;

import com.andy.spring.type.BaseStrType;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

/**
 * BaseStrType的myBaseTypeHandler
 *
 * @author 庞先海 2019-11-17
 */
public class MyBaseStrTypeHandler<T extends BaseStrType> extends BaseTypeHandler<T> {

    private List<T> values;


    public MyBaseStrTypeHandler(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        values = BaseStrType.getValues(type);
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getCode());
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String code = rs.getString(columnName);
        if (rs.wasNull()) {
            return null;
        } else {
            return parse(code);
        }
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String code = rs.getString(columnIndex);
        if (rs.wasNull()) {
            return null;
        } else {
            return parse(code);
        }
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String code = cs.getString(columnIndex);
        if (cs.wasNull()) {
            return null;
        } else {
            return parse(code);
        }
    }

    private T parse(String code) {
        try {
            return BaseStrType.parse(values, code);
        } catch (RuntimeException e) {
            //todo 日志
            return null;
        }
    }
}
