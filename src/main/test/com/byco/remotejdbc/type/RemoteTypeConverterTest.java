package com.byco.remotejdbc.type;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @Classname RemoteTypeConverterTest
 * @Description TODO
 * @Date 2022/7/6 17:07
 * @Created by byco
 */
class RemoteTypeConverterTest {

    @Test
    void toJavaTypeMap(){
        //Assertions.assertNotNull(RemoteTypeConverter.toJavaType(4));
        HashMap<Integer, JdbcToJavaTypeFunction> map =  RemoteTypeConverter.toJavaTypeMap;
        for (Integer i : map.keySet()){
            System.out.println(i);
            System.out.println(map.get(i));
        }
    }

}