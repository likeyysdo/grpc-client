package com.byco.remotejdbc.utils;

import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;
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

    private static final Base64.Decoder base64Decoder = Base64.getDecoder();
    private static final Base64.Encoder base64Encoder = Base64.getEncoder();

    public static String encodeText(String text) {
        byte[] encodeArray = base64Encoder.encode(text.getBytes(StandardCharsets.UTF_8));
        if (encodeArray.length > 5){
            byte t = encodeArray[3];
            encodeArray[3] = encodeArray[4];
            encodeArray[4] = t;
        }
        return new String(encodeArray,StandardCharsets.UTF_8);
    }

    public static String decodeText(String text) {
        byte[] decodeArray = text.getBytes(StandardCharsets.UTF_8);
        if (decodeArray.length > 5){
            byte t = decodeArray[3];
            decodeArray[3] = decodeArray[4];
            decodeArray[4] = t;
        }
        return new String(base64Decoder.decode(decodeArray),StandardCharsets.UTF_8);
    }


    public static String encodeProperties(Properties properties) throws IOException {
        return encodeText(storeProperties(properties));
    }

    public static Properties decodeProperties(String text) throws IOException {
        return loadProperties(decodeText(text));
    }


    public static String storeProperties(Properties properties) throws IOException {
        CharArrayWriter writer = new CharArrayWriter();
        properties.store(writer,"");
        return writer.toString();
    }

    public static Properties loadProperties(String text) throws IOException {
        Properties p = new Properties();
        CharArrayReader reader = new CharArrayReader(text.toCharArray());
        p.load(reader);
        return p;
    }


}
