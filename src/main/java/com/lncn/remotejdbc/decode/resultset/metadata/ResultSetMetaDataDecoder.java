package com.lncn.remotejdbc.decode.resultset.metadata;

import java.io.IOException;
import java.sql.ResultSetMetaData;

/**
 * @Classname ResultSetMetaDataEncoder
 * @Description TODO
 * @Date 2022/7/8 15:07
 * @Created by byco
 */
public interface ResultSetMetaDataDecoder {
    ResultSetMetaData decode(byte[] bytes) throws IOException;
}
