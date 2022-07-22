package com.lncn.remotejdbc.decode;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
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

}