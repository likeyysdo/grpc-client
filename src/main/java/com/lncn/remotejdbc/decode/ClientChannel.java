package com.lncn.remotejdbc.decode;

import com.lncn.remotejdbc.utils.CodeUtils;
import com.lncn.remotejdbc.utils.Logger;
import com.google.common.base.Strings;
import com.lncn.remotejdbc.utils.LoggerFactory;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Classname ClientChannel
 * @Description TODO
 * @Date 2022/7/19 16:33
 * @Created by byco
 */
public class ClientChannel {

    private static final Logger log = LoggerFactory.getLogger(ClientChannel.class);

    private static final int DEFAULT_BUFFER_CAPACITY = 2000;
    private static final int DEFAULT_TIMEOUT_SECONDS = 30;

    private String url;
    private Properties properties;
    private ConcurrentHashMap<ClientStub,String> stubSet;

    private String encodeProperties;

    private ManagedChannel channel;

    private boolean closed;
    private int bufferCapacity;
    private long timeOutSeconds;

    private ClientChannel() {

    }

    public ClientChannel(String url, Properties properties) throws SQLException {
        log.debug("new ClientChannel url:",url);
        if(Strings.isNullOrEmpty(url)) throw new IllegalArgumentException("url is empty");
        this.url = url;
        this.properties = properties;
        propertiesInitialize();
        channel =  ManagedChannelBuilder.forTarget(url)
            .usePlaintext()
            .build();
        stubSet = new ConcurrentHashMap<>();
    }

    public String getEncodeProperties() {
        return encodeProperties;
    }

    void propertiesEncode() throws SQLException {
        log.debug("propertiesEncode");
        try {
            encodeProperties = CodeUtils.encodeProperties(this.properties);
        } catch (IOException e) {
            throw new RJdbcSQLException("Properties serialize failed" + this.properties,e);
        }
    }

    void propertiesInitialize() throws SQLException {
        log.debug("propertiesInitialize start");
        propertiesEncode();
        initializeLogLevel();
        initializeFetchSize();
        initializeTimeOut();
        log.debug("propertiesInitialize end");
    }


    void initializeLogLevel(){
        log.debug("Initialize logLevel start ", Logger.getGlobalLogLevel());
        String logLevelString =  getNotNullProperty("logLevel").toUpperCase();
        if( !isNotNullPropertyValue(logLevelString) ) return;
        try{
            Logger.LogLevel logLevel = Logger.LogLevel.valueOf(logLevelString);
            Logger.setGlobalLogLevel(logLevel);
        }catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Wrong logLevel "+ logLevelString + " support log level: "
            + Arrays.toString(Logger.LogLevel.values()));
        }
        log.debug("Initialize logLevel end ", Logger.getGlobalLogLevel());
    }

    void initializeFetchSize(){
        String fetchSizeString =  getNotNullProperty("fetchSize");
        int bufferCapacity = DEFAULT_BUFFER_CAPACITY;
        if( isNotNullPropertyValue(fetchSizeString) ){
            bufferCapacity = Integer.parseInt(fetchSizeString);
        }
        this.bufferCapacity = bufferCapacity;
        log.debug("Initialize bufferCapacity",this.bufferCapacity);
    }

    void initializeTimeOut(){
        String timeOutString =  getNotNullProperty("timeOut");
        int timeOutSeconds = DEFAULT_TIMEOUT_SECONDS;
        if( isNotNullPropertyValue(timeOutString) ){
            timeOutSeconds = Integer.parseInt(timeOutString);
        }
        this.timeOutSeconds = timeOutSeconds;
        log.debug("Initialize timeOutSeconds",this.timeOutSeconds);
    }



    public long getTimeOutSeconds() {
        return timeOutSeconds;
    }

    boolean isNotNullPropertyValue(String property){
        return !"".equals(property);
    }

    String getNotNullProperty( String property ){
        if( properties == null ) return "";
        String propertyValue = properties.getProperty(property);
        if( propertyValue != null && !propertyValue.trim().isBlank() ){
            return propertyValue;
        }else{
            return "";
        }
    }

    public ClientStub getStub() throws RJdbcSQLException {
        log.debug("ClientChannel getStub");
        ClientStub stub  = new ClientStub(this);
        stubSet.put(stub,"");
        return stub;
    }

    void closeStub(ClientStub stub){
        stub.close();
        stubSet.remove(stub);
    }

    ManagedChannel getChannel() {
        return channel;
    }

    public int getBufferCapacity() {
        return bufferCapacity;
    }


    public void close() throws SQLException {
        if( closed ) return;
        for( ClientStub stub : stubSet.keySet() ){
            closeStub(stub);
        }
        try {
            channel.shutdownNow().awaitTermination(15, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
        closed = true;
    }

    public String getUrl() {
        return url;
    }

    public Properties getProperties() {
        return properties;
    }

    public boolean isClosed() {
        return closed;
    }
}
