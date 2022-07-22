package com.lncn.remotejdbc.encode;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @Classname JdbcToJavaTypeFunction
 * @Description TODO
 * @Date 2022/7/6 16:10
 * @Created by byco
 */
@FunctionalInterface
public interface ResultRowEncodeConsumer<T,U,O>  {
    void apply(T index, U resultSet , O codedOutputStream) throws IOException, SQLException;
}
