package com.byco.remotejdbc.decode;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Properties;

/**
 * @Classname CodeUtils
 * @Description TODO
 * @Date 2022/7/21 17:55
 * @Created by byco
 */
public class CodeUtils {

    private static final String salt = "salt";
    private static final Base64.Decoder base64Decoder = Base64.getDecoder();
    private static final Base64.Encoder base64Encoder = Base64.getEncoder();

    public static String encodeSQL(String text){

        byte[] r =  base64Encoder.encode(text.getBytes());
return "";
    }
    public static String decodeSQL(String text){
        return "";
    }

    public static String encodeProperties(String text){
        return "";
    }
    public static Properties decodeProperties(String text){
        return null;
    }


}
