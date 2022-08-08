package com.lncn.remotejdbc.utils;

import com.lncn.remotejdbc.constant.Constants;
import java.util.Calendar;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.LoggerFactory;

/**
 * @Classname Log
 * @Description TODO
 * @Date 2022/7/20 11:47
 * @Created by byco
 */
public class Logger {
    private final static org.slf4j.Logger log = LoggerFactory.getLogger(Constants.DATABASE_PRODUCT_NAME);



    private static boolean simpleLog = false;

    public static boolean isSimpleLog() {
        return simpleLog;
    }

    public static void setSimpleLog(boolean simpleLog) {
        Logger.simpleLog = simpleLog;
    }


    public enum LogLevel{
        OFF
        ,ERROR
        ,WARN
        ,INFO
        ,DEBUG
        ,ALL;

    }

    boolean isSkippedLogLevel(LogLevel type){
        return globaLogLevel.ordinal() < type.ordinal();
    }


    private static LogLevel globaLogLevel = LogLevel.ERROR;
    private final Class aClass;
    private final String className;


    public static void setGlobalLogLevel(LogLevel level){
        globaLogLevel = level;
    }

    public static LogLevel getGlobalLogLevel(){
        return globaLogLevel;
    }

    Logger(Class aClass) {
        this.aClass = aClass;
        this.className = " [" + aClass.getName() + "] ";
    }



    private StringBuilder getTimeHead(){
        Calendar calendar = Calendar.getInstance();
        StringBuilder sb = new StringBuilder(30);
        sb.append(calendar.get(Calendar.YEAR) )
            .append("-")
            .append(calendar.get(Calendar.MONTH))
            .append("-")
            .append(calendar.get(Calendar.DATE))
            .append(" ")
            .append(calendar.get(Calendar.HOUR_OF_DAY))
            .append(":")
            .append(calendar.get(Calendar.MINUTE))
            .append(":")
            .append(calendar.get(Calendar.SECOND));
        return sb;
    }





    public  void info(Object ... elements){

        printElements(LogLevel.INFO , elements);
    }
    public  void warn(Object ... elements){

        printElements(LogLevel.WARN , elements);
    }
    public  void debug(Object ... elements){

        printElements(LogLevel.DEBUG , elements);
    }
    public  void error(Object ... elements){

        printElements(LogLevel.ERROR , elements);
    }
    public  void error(String s , Throwable t){

        printElements(LogLevel.ERROR,s, t.getMessage());
    }

    public  void error(Throwable t){

        printElements(LogLevel.ERROR ,  t.getMessage());
    }
    public void debugError(Throwable t){
        if(isSkippedLogLevel(LogLevel.DEBUG))return;
        t.printStackTrace();
    }


     void printElements(LogLevel type, Object ... elements ){
         if( simpleLog ){
             printElementsRaw(type, elements);
         }else{
             printElementsWithLogger(type, elements);
         }
    }

    void printElementsWithLogger(LogLevel type, Object ... elements ){
        if(isSkippedLogLevel(type))return;
        StringBuilder sb = new StringBuilder()
            .append(this.className);
        for( Object o : elements ){
            sb.append(o).append(" ");
        }
        switch(type){
            case DEBUG: log.debug(sb.toString());break;
            case INFO: log.info(sb.toString());break;
            case WARN: log.warn(sb.toString());break;
            case ERROR: log.error(sb.toString());break;
            default:log.debug(sb.toString());
        }
    }

    void printElementsRaw(LogLevel type, Object ... elements ){
        if(isSkippedLogLevel(type))return;
        StringBuilder sb = getTimeHead();
        sb.append(" ")
            .append(type.name())
            .append(this.className)
            .append("(")
            .append(Thread.currentThread().getName())
            .append(") ")
        ;
        for( Object o : elements ){
            sb.append(o).append(" ");
        }
        System.out.println(sb.toString());
    }

}
