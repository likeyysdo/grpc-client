package com.lncn.remotejdbc.decode.resultset;

import com.google.protobuf.CodedInputStream;
import com.lncn.remotejdbc.decode.resultset.resultrow.ResultRowDecodeFunction;
import com.lncn.remotejdbc.type.RemoteType;

/**
 * @Classname RemoteTypeConverter
 * @Description TODO
 * @Date 2022/7/6 15:48
 * @Created by byco
 */
public class RawDoubleResultRowDecoder extends  DefaultResultRowDecoder {

    private static final ResultRowDecodeFunction<CodedInputStream, Float> decodeRawFloat;
    private static final ResultRowDecodeFunction<CodedInputStream, Double> decodeRawDouble;


    static {
        decodeRawFloat = CodedInputStream::readFloat;
        decodeRawDouble = CodedInputStream::readDouble;
        directDecodeMap.put(RemoteType.REAL.jdbcType, RawDoubleResultRowDecoder.decodeRawFloat);
        directDecodeMap.put(RemoteType.FLOAT.jdbcType, RawDoubleResultRowDecoder.decodeRawDouble);
        directDecodeMap.put(RemoteType.DOUBLE.jdbcType, RawDoubleResultRowDecoder.decodeRawDouble);
    }

}
