package com.lncn.remotejdbc.encode;

import com.google.protobuf.CodedOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * @Classname ResultRowEncoder
 * @Description TODO
 * @Date 2022/7/7 17:36
 * @Created by byco
 */
public class ResultRowEncodeFactory {
    private ResultSetMetaData resultSetMetaData;
    private FastByteArrayOutputStream array;
    private CodedOutputStream codedOutputStream;
    private ResultRowEncodeConsumer[] codeFunctionList;
    private int size;

    private ResultRowEncodeFactory(ResultSetMetaData resultSetMetaData,
                                   ResultRowEncoder converter,
                                   FastByteArrayOutputStream array,
                                   CodedOutputStream codedOutputStream) {
        this.resultSetMetaData = resultSetMetaData;
        this.array = array;
        this.codedOutputStream = codedOutputStream;
    }

    private ResultRowEncodeFactory( ) {

    }

    public void add( int index, ResultSet rs ) throws SQLException, IOException {
        codeFunctionList[index - 1].apply(index,rs,codedOutputStream);
    }

    public void add( ResultSet rs ) throws SQLException, IOException {
        ResultSet resultSet = rs;
        int size = this.size;
        for( int i = 1 ; i <= size ; i++){
            codeFunctionList[i - 1].apply(i,resultSet,codedOutputStream);
        }
    }

    public byte[] encode() throws IOException {
        codedOutputStream.flush();
        byte[] r = array.toByteArray();
        array.reset();
        return r;
    }

    public void reset(){
        array.reset();
    }

    public static class Builder {
        private ResultSetMetaData resultSetMetaData;
        private ResultRowEncoder converter;
        private FastByteArrayOutputStream array;
        private CodedOutputStream codedOutputStream;
        private int blockSize = 0;

        public Builder(ResultSetMetaData resultSetMetaData ){
            this.resultSetMetaData = resultSetMetaData;
        }

        Builder setResultRowConverter(ResultRowEncoder converter){
            this.converter = converter;
            return this;
        }
        Builder setFastByteArrayOutputStream(FastByteArrayOutputStream array){
            this.array = array;
            return this;
        }
        Builder setCodedOutputStream(CodedOutputStream codedOutputStream){
            this.codedOutputStream = codedOutputStream;
            return this;
        }

        Builder setBlockSize(int blockSize){
            this.blockSize = blockSize;
            return this;
        }


        public ResultRowEncodeFactory build() throws SQLException {
            if (converter == null){
                this.converter = new DefaultResultRowEncoder();
            }
            if( array == null ) {
                if( blockSize == 0){
                    this.array = new FastByteArrayOutputStream();
                }else{
                    this.array = new FastByteArrayOutputStream(blockSize);
                }
            }
            if( codedOutputStream == null ){
                this.codedOutputStream = CodedOutputStream.newInstance(array);
            }
            ResultRowEncodeFactory encoder =  new ResultRowEncodeFactory();
            encoder.resultSetMetaData = this.resultSetMetaData;
            encoder.array = this.array;
            encoder.codedOutputStream = this.codedOutputStream;
            encoder.size = resultSetMetaData.getColumnCount();
            ResultRowEncodeConsumer[] functionList = new ResultRowEncodeConsumer[encoder.size];
            for( int i = 1 ; i <= encoder.size ; i++ ){
                functionList[i-1] = converter.getEncoder(resultSetMetaData.getColumnType(i));
            }
            encoder.codeFunctionList = functionList;
            return encoder;
        }
    }
}
