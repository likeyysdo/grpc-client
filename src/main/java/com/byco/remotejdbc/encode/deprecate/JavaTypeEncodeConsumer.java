package com.byco.remotejdbc.encode.deprecate;

import java.io.IOException;
import java.sql.SQLException;

/**
 * @Classname JdbcToJavaTypeFunction
 * @Description TODO
 * @Date 2022/7/6 16:10
 * @Created by byco
 */
@FunctionalInterface
public interface JavaTypeEncodeConsumer<T,U>  {
    void apply(T codedOutputStream, U value) throws IOException;
}
