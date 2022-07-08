package com.byco.remotejdbc.encode.deprecate;

import com.byco.remotejdbc.type.RemoteType;
import com.google.protobuf.CodedOutputStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;

/**
 * @Classname ResultRowEncoderConverter
 * @Description TODO
 * @Date 2022/7/7 14:56
 * @Created by byco
 */
public class ResultRowEncoderConverter {
    private static final HashMap<Integer, JavaTypeEncodeConsumer> JavaTypeEncoderMap;
    private static final JavaTypeEncodeConsumer<CodedOutputStream, Integer> writeSInt32;
    private static final JavaTypeEncodeConsumer<CodedOutputStream, Long> writeSInt64;
    private static final JavaTypeEncodeConsumer<CodedOutputStream, String> writeString;
    private static final JavaTypeEncodeConsumer<CodedOutputStream, Boolean> writeBool;
    private static final JavaTypeEncodeConsumer<CodedOutputStream, byte[]> writeByteArray;
    private static final JavaTypeEncodeConsumer<CodedOutputStream, Date> writeDate;
    private static final JavaTypeEncodeConsumer<CodedOutputStream, Time> writeTime;
    private static final JavaTypeEncodeConsumer<CodedOutputStream, Timestamp> writeTimestamp;
    private static final JavaTypeEncodeConsumer<CodedOutputStream, BigDecimal> writeBigDecimal;
    private static final JavaTypeEncodeConsumer<CodedOutputStream, Float> writeFloat;
    private static final JavaTypeEncodeConsumer<CodedOutputStream, Double> writeDouble;

    public static JavaTypeEncodeConsumer getEncoder(Integer i) {
        return JavaTypeEncoderMap.get(i);
    }

    static {
        writeSInt32 = CodedOutputStream::writeSInt32NoTag;
        writeSInt64 = CodedOutputStream::writeSInt64NoTag;
        writeString = CodedOutputStream::writeStringNoTag;
        writeBool = CodedOutputStream::writeBoolNoTag;
        writeByteArray = CodedOutputStream::writeByteArrayNoTag;
        writeDate = (x, y) -> {
            x.writeUInt64NoTag(y.getTime());
        };
        writeTime = (x, y) -> {
            x.writeUInt64NoTag(y.getTime());
        };
        writeTimestamp = (x, y) -> {
            x.writeUInt64NoTag(y.getTime());
        };
        writeBigDecimal = (x,y) ->{
            x.writeSInt64NoTag(y.unscaledValue().longValue());
        };

        writeFloat = (x,y) ->{
            x.writeSInt64NoTag((long) ( y * 1000_000 ));
        };
        writeDouble = (x,y) ->{
            x.writeSInt64NoTag((long) ( y * 1000_000 ));
        };

        JavaTypeEncoderMap = new HashMap<>(15);
        JavaTypeEncoderMap.put(RemoteType.CHAR.jdbcType, writeString);
        JavaTypeEncoderMap.put(RemoteType.VARCHAR.jdbcType, writeString);
        JavaTypeEncoderMap.put(RemoteType.LONGVARCHAR.jdbcType, writeString);
        JavaTypeEncoderMap.put(RemoteType.NUMERIC.jdbcType, writeBigDecimal);
        JavaTypeEncoderMap.put(RemoteType.DECIMAL.jdbcType, writeBigDecimal);
        JavaTypeEncoderMap.put(RemoteType.BIT.jdbcType, writeBool);
        JavaTypeEncoderMap.put(RemoteType.BOOLEAN.jdbcType, writeBool);
        JavaTypeEncoderMap.put(RemoteType.TINYINT.jdbcType, writeSInt32);
        JavaTypeEncoderMap.put(RemoteType.SMALLINT.jdbcType, writeSInt32);
        JavaTypeEncoderMap.put(RemoteType.INTEGER.jdbcType, writeSInt32);
        JavaTypeEncoderMap.put(RemoteType.BIGINT.jdbcType, writeSInt64);
        JavaTypeEncoderMap.put(RemoteType.REAL.jdbcType, writeFloat);
        JavaTypeEncoderMap.put(RemoteType.FLOAT.jdbcType, writeDouble);
        JavaTypeEncoderMap.put(RemoteType.DOUBLE.jdbcType, writeDouble);
        JavaTypeEncoderMap.put(RemoteType.BINARY.jdbcType, writeByteArray);
        JavaTypeEncoderMap.put(RemoteType.VARBINARY.jdbcType, writeByteArray);
        JavaTypeEncoderMap.put(RemoteType.LONGVARBINARY.jdbcType, writeByteArray);
        JavaTypeEncoderMap.put(RemoteType.DATE.jdbcType, writeDate);
        JavaTypeEncoderMap.put(RemoteType.TIME.jdbcType, writeTime);
        JavaTypeEncoderMap.put(RemoteType.TIMESTAMP.jdbcType, writeTimestamp);
    }
}
