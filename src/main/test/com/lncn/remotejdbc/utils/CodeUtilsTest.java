package com.lncn.remotejdbc.utils;

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
    public void EmptyStatementBase64EnDecode_Test(){
        //Arrange
        String s1 = null;
        String s2 = "";
        //Act
        String r1 = CodeUtils.decodeText(CodeUtils.encodeText(s1));
        String r2 = CodeUtils.decodeText(CodeUtils.encodeText(s2));
        //Assert
        Assertions.assertEquals("",r1);
        Assertions.assertEquals("",r2);
    }
    @Test
    public void EmptyPropertiesEnDecode_Test() throws IOException {
        //Arrange
        Properties p1 = new Properties();
        Properties p2 = null;
        //Act
        Properties r1 = CodeUtils.loadProperties(CodeUtils.storeProperties(p1));
        Properties r2 = CodeUtils.loadProperties(CodeUtils.storeProperties(p2));
        //Assert
        Assertions.assertTrue(r1.isEmpty());
        Assertions.assertTrue(r2.isEmpty());
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