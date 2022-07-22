package com.lncn.remotejdbc.decode.resultset.resultrow;

import java.io.IOException;

/**
 * @Classname JdbcToJavaTypeFunction
 * @Description TODO
 * @Date 2022/7/6 16:10
 * @Created by byco
 */
@FunctionalInterface
public interface ResultRowDecodeFunction<T,R>  {
    R apply(T codedInputStream) throws IOException;
}
