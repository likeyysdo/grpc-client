package com.lncn.remotejdbc.type;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.HashMap;

/**
 * @Classname RemoteTypeConverter
 * @Description TODO
 * @Date 2022/7/6 15:48
 * @Created by byco
 */
public class RemoteTypeConverter {
    public static final HashMap<Integer, JdbcToJavaTypeFunction> toJavaTypeMap;
    static final JdbcToJavaTypeFunction<Integer, ResultSet, Integer> getInteger;
    static final JdbcToJavaTypeFunction<Integer, ResultSet, Long> getLong;
    static final JdbcToJavaTypeFunction<Integer, ResultSet, Float> getFloat;
    static final JdbcToJavaTypeFunction<Integer, ResultSet, Double> getDouble;
    static final JdbcToJavaTypeFunction<Integer, ResultSet, String> getString;
    static final JdbcToJavaTypeFunction<Integer, ResultSet, Boolean> getBoolean;
    static final JdbcToJavaTypeFunction<Integer, ResultSet, BigDecimal> getBigDecimal;
    static final JdbcToJavaTypeFunction<Integer, ResultSet, java.sql.Date> getDate;
    static final JdbcToJavaTypeFunction<Integer, ResultSet, java.sql.Time> getTime;
    static final JdbcToJavaTypeFunction<Integer, ResultSet, java.sql.Timestamp> getTimestamp;
    static final JdbcToJavaTypeFunction<Integer, ResultSet, byte[]> getByteArray;
    static {
        getInteger = (x, y) -> {
            return y.getInt(x);
        };
        getLong = (x, y) -> {
            return y.getLong(x);
        };
        getFloat = (x, y) -> {
            return y.getFloat(x);
        };
        getDouble = (x, y) -> {
            return y.getDouble(x);
        };
        getString = (x, y) -> {
            return y.getString(x);
        };
        getBoolean = (x, y) -> {
            return y.getBoolean(x);
        };
        getBigDecimal = (x, y) -> {
            return y.getBigDecimal(x);
        };
        getDate = (x, y) -> {
            return y.getDate(x);
        };
        getTime = (x, y) -> {
            return y.getTime(x);
        };
        getTimestamp = (x, y) -> {
            return y.getTimestamp(x);
        };
        getByteArray = (x, y) -> {
            return y.getBytes(x);
        };

        toJavaTypeMap = new HashMap<>(27);
        toJavaTypeMap.put(RemoteType.CHAR.jdbcType, RemoteTypeConverter.getString);
        toJavaTypeMap.put(RemoteType.VARCHAR.jdbcType, RemoteTypeConverter.getString);
        toJavaTypeMap.put(RemoteType.LONGVARCHAR.jdbcType, RemoteTypeConverter.getString);
        toJavaTypeMap.put(RemoteType.NUMERIC.jdbcType, RemoteTypeConverter.getBigDecimal);
        toJavaTypeMap.put(RemoteType.DECIMAL.jdbcType, RemoteTypeConverter.getBigDecimal);
        toJavaTypeMap.put(RemoteType.BIT.jdbcType, RemoteTypeConverter.getBoolean);
        toJavaTypeMap.put(RemoteType.BOOLEAN.jdbcType, RemoteTypeConverter.getBoolean);
        toJavaTypeMap.put(RemoteType.TINYINT.jdbcType, RemoteTypeConverter.getInteger);
        toJavaTypeMap.put(RemoteType.SMALLINT.jdbcType, RemoteTypeConverter.getInteger);
        toJavaTypeMap.put(RemoteType.INTEGER.jdbcType, RemoteTypeConverter.getInteger);
        toJavaTypeMap.put(RemoteType.BIGINT.jdbcType, RemoteTypeConverter.getLong);
        toJavaTypeMap.put(RemoteType.REAL.jdbcType, RemoteTypeConverter.getFloat);
        toJavaTypeMap.put(RemoteType.FLOAT.jdbcType, RemoteTypeConverter.getDouble);
        toJavaTypeMap.put(RemoteType.DOUBLE.jdbcType, RemoteTypeConverter.getDouble);
        toJavaTypeMap.put(RemoteType.BINARY.jdbcType, RemoteTypeConverter.getByteArray);
        toJavaTypeMap.put(RemoteType.VARBINARY.jdbcType, RemoteTypeConverter.getByteArray);
        toJavaTypeMap.put(RemoteType.LONGVARBINARY.jdbcType, RemoteTypeConverter.getByteArray);
        toJavaTypeMap.put(RemoteType.DATE.jdbcType, RemoteTypeConverter.getDate);
        toJavaTypeMap.put(RemoteType.TIME.jdbcType, RemoteTypeConverter.getTime);
        toJavaTypeMap.put(RemoteType.TIMESTAMP.jdbcType, RemoteTypeConverter.getTimestamp);
    }

    public static JdbcToJavaTypeFunction toJavaType(Integer i) {
        return toJavaTypeMap.get(i);
    }

}
