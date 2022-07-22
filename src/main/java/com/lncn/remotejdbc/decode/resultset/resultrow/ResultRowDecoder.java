package com.lncn.remotejdbc.decode.resultset.resultrow;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * @Classname ResultRowConverter
 * @Description TODO
 * @Date 2022/7/7 17:42
 * @Created by byco
 */
public interface ResultRowDecoder {
    ResultRowDecodeFunction getDecoder(Integer i);
    ResultRowDecodeFunction getDecoder(ResultSetMetaData resultSetMetaData , int column) throws SQLException;
}
