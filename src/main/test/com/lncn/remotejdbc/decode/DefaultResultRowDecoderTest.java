package com.lncn.remotejdbc.decode;

import static org.junit.jupiter.api.Assertions.*;

import com.google.type.DateTime;
import java.math.BigDecimal;
import java.util.Date;
import org.junit.jupiter.api.Test;

/**
 * @Classname DefaultResultRowDecoderTest
 * @Description TODO
 * @Date 2022/7/8 14:11
 * @Created by byco
 */
class DefaultResultRowDecoderTest {


    @Test
    void bigDecimal(){
        BigDecimal b = BigDecimal.valueOf(12345,2);
        System.out.println(b);
    }

    @Test
    public void Time_Test(){
        //Arrange
        long today = new Date().getTime();
        //Act
        System.out.println(today);
        System.out.println(new java.sql.Timestamp(today));
        //Assert
    }
}