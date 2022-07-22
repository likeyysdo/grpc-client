package com.lncn.remotejdbc.type;

import java.sql.SQLException;

/**
 * @Classname JdbcToJavaTypeFunction
 * @Description TODO
 * @Date 2022/7/6 16:10
 * @Created by byco
 */
@FunctionalInterface
public interface JdbcToJavaTypeFunction<T,U,R> {
    R apply(T index, U resultSet) throws SQLException;
}
