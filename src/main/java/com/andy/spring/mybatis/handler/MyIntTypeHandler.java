package com.andy.spring.mybatis.handler;

import com.andy.spring.type.IntType;
import com.andy.spring.util.ObjectUtil;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

/**
 * IntType的myBaseTypeHandler
 *
 * @author 庞先海 2019-11-17
 */
public class MyIntTypeHandler<T extends IntType> extends BaseTypeHandler<T> {

    private Class<T> type;

    public MyIntTypeHandler(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getValue());
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        int value = rs.getInt(columnName);
        T intType = ObjectUtil.instantiateClass(type);
        intType.setValue(value);
        return intType;
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int value = rs.getInt(columnIndex);
        T intType = ObjectUtil.instantiateClass(type);
        intType.setValue(value);
        return intType;
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        int value = cs.getInt(columnIndex);
        T intType = ObjectUtil.instantiateClass(type);
        intType.setValue(value);
        return intType;
    }
}
