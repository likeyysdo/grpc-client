package com.lncn.remotejdbc.decode.resultset.metadata;

import com.lncn.remotejdbc.encode.FastByteArrayOutputStream;
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
public class DefaultResultSetMetaDataEncoder implements ResultSetMetaDataEncoder{
    @Override
    public byte[] encode(ResultSetMetaData metaData) throws SQLException, IOException {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        CodedOutputStream output = CodedOutputStream.newInstance(outputStream);
        int count = metaData.getColumnCount();
        output.writeUInt32NoTag(count);
        for( int i = 1 ; i <= count ; i++ ){
            output.writeStringNoTag(metaData.getColumnLabel(i));
            output.writeStringNoTag(metaData.getColumnName(i));
            output.writeUInt32NoTag(metaData.getScale(i));
            output.writeUInt32NoTag(metaData.getColumnType(i));
        }
        output.flush();
        return outputStream.toByteArray();
    }
}
