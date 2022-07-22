package com.byco.remotejdbc.utils;

import com.byco.remotejdbc.utils.CodeUtils;
import java.io.IOException;
import java.util.Properties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @Classname CodeUtilsTest
 * @Description TODO
 * @Date 2022/7/22 11:06
 * @Created by byco
 */
class CodeUtilsTest {

    @Test
    public void StatementBaseEncodeDecode_Test(){
        //Arrange
        String test = "TestString123!@#";
        //Act
        String result = CodeUtils.decodeText(CodeUtils.encodeText(test));
        //Assert
        Assertions.assertEquals(test,result);
    }
    @Test
    public void PropertiesSaveLoad_Test() throws IOException {
        //Arrange
        Properties properties = new Properties();
        properties.setProperty("name","dummy");
        properties.setProperty("key","value");
        //Act
        Properties result = CodeUtils.loadProperties(CodeUtils.storeProperties(properties));
        //Assert
        Assertions.assertEquals(properties,result);
    }
    @Test
    public void PropertiesEncodeDecode_Test() throws IOException {
        //Arrange
        Properties properties = new Properties();
        properties.setProperty("name","dummy");
        properties.setProperty("key","value");
        //Act
        Properties result = CodeUtils.decodeProperties(CodeUtils.encodeProperties(properties));
        //Assert
        Assertions.assertEquals(properties,result);

    }
}