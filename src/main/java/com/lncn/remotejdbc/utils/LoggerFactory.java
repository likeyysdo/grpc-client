package com.lncn.remotejdbc.utils;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Classname LoggerFactory
 * @Description TODO
 * @Date 2022/8/5 11:08
 * @Created by byco
 */
public class LoggerFactory {

    public static Logger getLogger(Class aClass){
        return  new Logger(aClass);
    }
}
