package com.lncn.remotejdbc.encode;

import com.google.protobuf.CodedOutputStream;
import java.sql.ResultSet;

/**
 * @Classname ResultRowConverter
 * @Description TODO
 * @Date 2022/7/7 17:42
 * @Created by byco
 */
public interface ResultRowEncoder {
    ResultRowEncodeConsumer<Integer, ResultSet, CodedOutputStream> getEncoder(Integer i);
}
