package com.byco.remotejdbc.decode.statement;

import com.byco.remotejdbc.decode.DefaultResultSetImpl;
import com.byco.remotejdbc.decode.ResultRowDecodeFactory;
import com.byco.remotejdbc.decode.resultrow.DefaultResultRow;
import com.byco.remotejdbc.decode.resultrow.ResultRow;
import com.byco.remotejdbc.metadata.DefaultResultSetMetaDataDecoder;
import com.byco.remotejdbc.utils.Log;
import com.google.common.base.Strings;
import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import io.quarkus.remote.ClientStatus;
import io.quarkus.remote.ServerStatus;
import io.quarkus.remote.SimpleStatementGrpc;
import io.quarkus.remote.SimpleStatementRequest;
import io.quarkus.remote.SimpleStatementResponse;
import java.io.IOException;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @Classname ClientChannel
 * @Description TODO
 * @Date 2022/7/19 16:33
 * @Created by byco
 */
public class ClientChannel {

    private static final Log log = new Log(ClientChannel.class);

    private static final int DEFAULT_BUFFER_CAPACITY = 2000;

    private String url;
    private Properties properties;
    private ConcurrentHashMap<ClientStub,String> stubSet;
    private String encodeProperties;

    private ManagedChannel channel;

    private boolean closed;
    private int bufferCapacity;

    private ClientChannel() {

    }

    public ClientChannel(String url, Properties properties) {
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

    void propertiesEncode(){

    }

    void propertiesInitialize(){
        log.debug("propertiesInitialize start");
        initializeLogLevel();
        initializeFetchSize();
        log.debug("propertiesInitialize end");
    }


    void initializeLogLevel(){
        log.debug("Initialize logLevel start ",Log.getGlobalLogLevel());
        String logLevelString =  getNotNullProperty("logLevel").toUpperCase();
        if( !isNotNullPropertyValue(logLevelString) ) return;
        try{
            Log.LogLevel logLevel = Log.LogLevel.valueOf(logLevelString);
            Log.setGlobalLogLevel(logLevel);
        }catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Wrong logLevel "+ logLevelString + " support log level: "
            + Arrays.toString(Log.LogLevel.values()));
        }
        log.debug("Initialize logLevel end ",Log.getGlobalLogLevel());
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

    public ClientStub getStub(){
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
