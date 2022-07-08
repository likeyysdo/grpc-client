package com.byco.remotejdbc.metadata;

import com.byco.remotejdbc.encode.FastByteArrayOutputStream;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import java.io.IOException;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * @Classname DefaultResultSetMetaDataEncoder
 * @Description TODO
 * @Date 2022/7/8 15:11
 * @Created by byco
 */
public class DefaultResultSetMetaDataDecoder implements ResultSetMetaDataDecoder{
    @Override
    public ResultSetMetaData decode(byte[] bytes) throws IOException {
        CodedInputStream input = CodedInputStream.newInstance(bytes);
        int count = input.readUInt32();
        Field[] fields = new Field[count];
        for( int i = 1 ; i <= count ; i++){
            Field field = new Field(
                input.readString()
                ,input.readString()
                ,input.readUInt32()
                ,input.readUInt32()
            );
            fields[i-1] = field;
        }
        return new DefaultResultSetMetaData(fields);
    }
}
