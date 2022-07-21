package com.byco.remotejdbc.utils;

import java.util.Calendar;

/**
 * @Classname Log
 * @Description TODO
 * @Date 2022/7/20 11:47
 * @Created by byco
 */
public class Log {

    private static final String LOG_LEVEL_ERROR = "ERROR";
    private static final String LOG_LEVEL_WARN = "WARN";
    private static final String LOG_LEVEL_INFO = "INFO";
    private static final String LOG_LEVEL_DEBUG = "DEBUG";

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
    private final Calendar calendar;

    public static void setGlobalLogLevel(LogLevel level){
        globaLogLevel = level;
    }

    public static LogLevel getGlobalLogLevel(){
        return globaLogLevel;
    }

    public Log(Class aClass) {
        this.aClass = aClass;
        this.className = " [" + aClass.getName() + "] ";
        calendar = Calendar.getInstance();
    }

    private StringBuilder getTimeHead(){
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
