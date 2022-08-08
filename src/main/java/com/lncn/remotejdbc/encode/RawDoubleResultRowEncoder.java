package com.lncn.remotejdbc.encode;

import com.google.protobuf.CodedOutputStream;
import com.lncn.remotejdbc.type.RemoteType;
import java.sql.ResultSet;
import java.util.HashMap;

/**
 * @Classname RemoteTypeConverter
 * @Description TODO
 * @Date 2022/7/6 15:48
 * @Created by byco
 */
public class RawDoubleResultRowEncoder extends DefaultResultRowEncoder {


    protected static final ResultRowEncodeConsumer<Integer, ResultSet, CodedOutputStream> encodeRawFloat;
    protected static final ResultRowEncodeConsumer<Integer, ResultSet, CodedOutputStream> encodeRawDouble;

    static {
        encodeRawFloat = (x, y, z) -> {
            z.writeFloatNoTag(y.getFloat(x));
        };
        encodeRawDouble = (x, y, z) -> {
            z.writeDoubleNoTag(y.getDouble(x));
        };

        directEncodeMap.put(RemoteType.REAL.jdbcType, encodeRawFloat);
        directEncodeMap.put(RemoteType.FLOAT.jdbcType, encodeRawDouble);
        directEncodeMap.put(RemoteType.DOUBLE.jdbcType, encodeRawDouble);
    }

    public ResultRowEncodeConsumer<Integer, ResultSet, CodedOutputStream> getEncoder(Integer i) {
        return RawDoubleResultRowEncoder.directEncodeMap.get(i);
    }

}
