package com.lncn.remotejdbc.decode.resultset.metadata;

import java.io.IOException;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * @Classname ResultSetMetaDataEncoder
 * @Description TODO
 * @Date 2022/7/8 15:07
 * @Created by byco
 */
public interface ResultSetMetaDataEncoder {
    byte[] encode(ResultSetMetaData resultSetMetaData) throws SQLException, IOException;
}
