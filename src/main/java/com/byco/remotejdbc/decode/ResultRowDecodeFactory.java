package com.byco.remotejdbc.decode;

import com.google.protobuf.CodedInputStream;
import java.io.IOException;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * @Classname ResultRowDecodeFactory
 * @Description TODO
 * @Date 2022/7/8 9:42
 * @Created by byco
 */
public class ResultRowDecodeFactory {
    private ResultSetMetaData resultSetMetaData;
    private ResultRowDecodeFunction[] decodeFunctionList;
    private int size;

    public Object[] read( byte[] bytes ) throws IOException {
        int size = this.size;
        byte[] source = bytes;
        CodedInputStream codedInputStream = CodedInputStream.newInstance(source);
        Object[] resultSet = new Object[size];
        for( int i = 0; i < size ; i++ ){
            resultSet[i] = decodeFunctionList[i].apply(codedInputStream);
        }
        return resultSet;
    }

    public ResultSetMetaData getResultSetMetaData() {
        return resultSetMetaData;
    }

    private ResultRowDecodeFactory(){

    }

    public static class Builder{
        private ResultSetMetaData resultSetMetaData;
        private ResultRowDecoder decoder;
        public Builder(ResultSetMetaData resultSetMetaData){
            this.resultSetMetaData = resultSetMetaData;
        }
        public Builder setResultRowDecoder(ResultRowDecoder decoder){
            this.decoder = decoder;
            return this;
        }

        public ResultRowDecodeFactory build() throws SQLException {
            if( decoder == null ){
                this.decoder = new DefaultResultRowDecoder();
            }
            ResultRowDecodeFactory decodeFactory = new ResultRowDecodeFactory();
            decodeFactory.resultSetMetaData = resultSetMetaData;
            int size = resultSetMetaData.getColumnCount();
            decodeFactory.size = size;
            ResultRowDecodeFunction[] functionList = new ResultRowDecodeFunction[size];
            for( int i = 1 ; i <=  size ; i++){
                functionList[i-1] = decoder.getDecoder(resultSetMetaData,i);
            }
            decodeFactory.decodeFunctionList = functionList;
            return decodeFactory;
        }
    }
}
