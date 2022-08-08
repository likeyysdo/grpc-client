package com.lncn.remotejdbc.decode.resultset;

import static org.junit.jupiter.api.Assertions.*;

import com.google.type.DateTime;
import java.sql.Time;
import java.util.Date;
import org.junit.jupiter.api.Test;

/**
 * @Classname DefaultResultRowDecoderTest
 * @Description TODO
 * @Date 2022/7/24 19:13
 * @Created by byco
 */
class DefaultResultRowDecoderTest {



    @Test
    public void Date_Test(){
        //Arrange
        Date date = new Date();
        //Act
        long l = date.getTime();
        //Assert
        System.out.println(l);
        System.out.println(new Time(l).toString());
    }

}