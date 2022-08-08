package com.lncn.remotejdbc;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/**
 * @Classname RemoteConnectionImplTest
 * @Description TODO
 * @Date 2022/8/5 15:52
 * @Created by byco
 */
class RemoteConnectionImplTest {
    
    @Test
    public void URLProperties_Test(){
        //Arrange
        String url = "jdbc:rsql://localhost:9000?fetchSize=1000&timeOut=5&logLevel=debug";
        //Act
        System.out.println(url.substring(url.indexOf('?')+1));
        String[] p =url.substring(url.indexOf('?')+1).split("&");
        for(String s : p){
            int index = s.indexOf('=');
            System.out.println(s.substring(0,index));
            System.out.println(s.substring(index+1));
        }
        //Assert
    }

}