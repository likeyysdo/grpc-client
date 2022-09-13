package com.lncn.remotejdbc.decode.resultset;

import com.lncn.remotejdbc.decode.resultset.resultrow.ResultRowDecodeFunction;
import com.lncn.remotejdbc.decode.resultset.resultrow.ResultRowDecoder;
import com.lncn.remotejdbc.type.RemoteType;
import com.google.protobuf.CodedInputStream;
import java.math.BigDecimal;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * @Classname RemoteTypeConverter
 * @Description TODO
 * @Date 2022/7/6 15:48
 * @Created by byco
 */
public class DefaultResultRowDecoder implements ResultRowDecoder {
    private static final int DOUBLE_SCALE = 1000_000;
    protected static final HashMap<Integer, ResultRowDecodeFunction>
        directDecodeMap;
    private static final ResultRowDecodeFunction<CodedInputStream, Integer> decodeInteger;
    private static final ResultRowDecodeFunction<CodedInputStream, Long> decodeLong;
    private static final ResultRowDecodeFunction<CodedInputStream, Float> decodeFloat;
    private static final ResultRowDecodeFunction<CodedInputStream, Double> decodeDouble;
    private static final ResultRowDecodeFunction<CodedInputStream, String> decodeString;
    private static final ResultRowDecodeFunction<CodedInputStream, Boolean> decodeBoolean;
    private static final ResultRowDecodeFunction<CodedInputStream, BigDecimal> decodeBigDecimal;
    private static final ResultRowDecodeFunction<CodedInputStream, java.sql.Date> decodeDate;
    private static final ResultRowDecodeFunction<CodedInputStream, java.sql.Time> decodeTime;
    private static final ResultRowDecodeFunction<CodedInputStream, java.sql.Timestamp>
        decodeTimestamp;
    private static final ResultRowDecodeFunction<CodedInputStream, byte[]> decodeByteArray;

    private static final ResultRowDecodeFunction[] defaultBigDecimalList;
    private static final ResultRowDecodeFunction<CodedInputStream, BigDecimal>
        decodeBigDecimal0Scale;
    private static final ResultRowDecodeFunction<CodedInputStream, BigDecimal>
        decodeBigDecimal1Scale;
    private static final ResultRowDecodeFunction<CodedInputStream, BigDecimal>
        decodeBigDecimal2Scale;
    private static final ResultRowDecodeFunction<CodedInputStream, BigDecimal>
        decodeBigDecimal3Scale;
    private static final ResultRowDecodeFunction<CodedInputStream, BigDecimal>
        decodeBigDecimal4Scale;
    private static final ResultRowDecodeFunction<CodedInputStream, BigDecimal>
        decodeBigDecimal5Scale;
    private static final ResultRowDecodeFunction<CodedInputStream, BigDecimal>
        decodeBigDecimal6Scale;
    private static final ResultRowDecodeFunction<CodedInputStream, BigDecimal>
        decodeBigDecimal7Scale;
    private static final ResultRowDecodeFunction<CodedInputStream, BigDecimal>
        decodeBigDecimal8Scale;

    static {
        decodeInteger = CodedInputStream::readSInt32;
        decodeLong = CodedInputStream::readSInt64;
        decodeFloat = (x) -> (float) x.readSInt64() / DOUBLE_SCALE;
        decodeDouble = (x) -> (double) x.readSInt64() / DOUBLE_SCALE;
        decodeString = CodedInputStream::readString;
        decodeBoolean = CodedInputStream::readBool;
        decodeBigDecimal = (x) -> BigDecimal.valueOf(x.readSInt64());
        decodeBigDecimal0Scale = (x) -> BigDecimal.valueOf(x.readSInt64(), 0);
        decodeBigDecimal1Scale = (x) -> BigDecimal.valueOf(x.readSInt64(), 1);
        decodeBigDecimal2Scale = (x) -> BigDecimal.valueOf(x.readSInt64(), 2);
        decodeBigDecimal3Scale = (x) -> BigDecimal.valueOf(x.readSInt64(), 3);
        decodeBigDecimal4Scale = (x) -> BigDecimal.valueOf(x.readSInt64(), 4);
        decodeBigDecimal5Scale = (x) -> BigDecimal.valueOf(x.readSInt64(), 5);
        decodeBigDecimal6Scale = (x) -> BigDecimal.valueOf(x.readSInt64(), 6);
        decodeBigDecimal7Scale = (x) -> BigDecimal.valueOf(x.readSInt64(), 7);
        decodeBigDecimal8Scale = (x) -> BigDecimal.valueOf(x.readSInt64(), 8);
        defaultBigDecimalList = new ResultRowDecodeFunction[9];
        defaultBigDecimalList[0] =  decodeBigDecimal0Scale;
        defaultBigDecimalList[1] =  decodeBigDecimal1Scale;
        defaultBigDecimalList[2] =  decodeBigDecimal2Scale;
        defaultBigDecimalList[3] =  decodeBigDecimal3Scale;
        defaultBigDecimalList[4] =  decodeBigDecimal4Scale;
        defaultBigDecimalList[5] =  decodeBigDecimal5Scale;
        defaultBigDecimalList[6] =  decodeBigDecimal6Scale;
        defaultBigDecimalList[7] =  decodeBigDecimal7Scale;
        defaultBigDecimalList[8] =  decodeBigDecimal8Scale;

        decodeDate = (x) -> {
            long s = x.readUInt64();
            if( s != 7L )  {
                return new java.sql.Date(s);
            }else{
                return null;
            }
        };
        decodeTime = (x) -> {
            long s = x.readUInt64();
            if( s != 7L )  {
                return new java.sql.Time(s);
            }else{
                return null;
            }
        };
        decodeTimestamp = (x) -> {
            long s = x.readUInt64();
            if( s != 7L )  {
                return new java.sql.Timestamp(s);
            }else{
                return null;
            }
        };

        decodeByteArray = CodedInputStream::readByteArray;

        directDecodeMap = new HashMap<>(27);
        directDecodeMap.put(RemoteType.CHAR.jdbcType, DefaultResultRowDecoder.decodeString);
        directDecodeMap.put(RemoteType.VARCHAR.jdbcType, DefaultResultRowDecoder.decodeString);
        directDecodeMap.put(RemoteType.LONGVARCHAR.jdbcType, DefaultResultRowDecoder.decodeString);
        directDecodeMap.put(RemoteType.NUMERIC.jdbcType, DefaultResultRowDecoder.decodeBigDecimal);
        directDecodeMap.put(RemoteType.DECIMAL.jdbcType, DefaultResultRowDecoder.decodeBigDecimal);
        directDecodeMap.put(RemoteType.BIT.jdbcType, DefaultResultRowDecoder.decodeBoolean);
        directDecodeMap.put(RemoteType.BOOLEAN.jdbcType, DefaultResultRowDecoder.decodeBoolean);
        directDecodeMap.put(RemoteType.TINYINT.jdbcType, DefaultResultRowDecoder.decodeInteger);
        directDecodeMap.put(RemoteType.SMALLINT.jdbcType, DefaultResultRowDecoder.decodeInteger);
        directDecodeMap.put(RemoteType.INTEGER.jdbcType, DefaultResultRowDecoder.decodeInteger);
        directDecodeMap.put(RemoteType.BIGINT.jdbcType, DefaultResultRowDecoder.decodeLong);
        directDecodeMap.put(RemoteType.REAL.jdbcType, DefaultResultRowDecoder.decodeFloat);
        directDecodeMap.put(RemoteType.FLOAT.jdbcType, DefaultResultRowDecoder.decodeDouble);
        directDecodeMap.put(RemoteType.DOUBLE.jdbcType, DefaultResultRowDecoder.decodeDouble);
        directDecodeMap.put(RemoteType.BINARY.jdbcType, DefaultResultRowDecoder.decodeByteArray);
        directDecodeMap.put(RemoteType.VARBINARY.jdbcType, DefaultResultRowDecoder.decodeByteArray);
        directDecodeMap.put(RemoteType.LONGVARBINARY.jdbcType,
            DefaultResultRowDecoder.decodeByteArray);
        directDecodeMap.put(RemoteType.DATE.jdbcType, DefaultResultRowDecoder.decodeDate);
        directDecodeMap.put(RemoteType.TIME.jdbcType, DefaultResultRowDecoder.decodeTime);
        directDecodeMap.put(RemoteType.TIMESTAMP.jdbcType, DefaultResultRowDecoder.decodeTimestamp);

        //jdbc 4.0
        directDecodeMap.put(RemoteType.NCHAR.jdbcType, DefaultResultRowDecoder.decodeString);
        directDecodeMap.put(RemoteType.NVARCHAR.jdbcType, DefaultResultRowDecoder.decodeString);
        directDecodeMap.put(RemoteType.LONGNVARCHAR.jdbcType, DefaultResultRowDecoder.decodeString);
    }

    protected final HashMap<Integer, ResultRowDecodeFunction> externalBigDecimalMap;

    public DefaultResultRowDecoder() {
        this.externalBigDecimalMap = new HashMap<>();
    }

    public ResultRowDecodeFunction getDecoder(Integer jdbcType) {
        return DefaultResultRowDecoder.directDecodeMap.get(jdbcType);
    }

    public ResultRowDecodeFunction getDecoder(ResultSetMetaData resultSetMetaData, int column)
        throws SQLException {
        int jdbcType = resultSetMetaData.getColumnType(column);
        if (jdbcType == RemoteType.NUMERIC.jdbcType || jdbcType == RemoteType.DECIMAL.jdbcType) {
            int sourceScale = resultSetMetaData.getScale(column);
            int scale = Math.max(sourceScale, 0);
            if (scale < defaultBigDecimalList.length) {
                return defaultBigDecimalList[scale];
            }else if (externalBigDecimalMap.containsKey(scale)) {
                return externalBigDecimalMap.get(scale);
            } else {
                ResultRowDecodeFunction<CodedInputStream, BigDecimal> tmpBigDecimalScale =
                    (x) -> BigDecimal.valueOf(x.readSInt64(), scale);
                externalBigDecimalMap.put(scale, tmpBigDecimalScale);
                return tmpBigDecimalScale;
            }
        } else {
            return DefaultResultRowDecoder.directDecodeMap.get(jdbcType);
        }
    }

}
