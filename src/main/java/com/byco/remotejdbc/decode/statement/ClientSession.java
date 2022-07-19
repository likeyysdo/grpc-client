package com.byco.remotejdbc.decode.statement;

import com.byco.remotejdbc.decode.ResultRowDecodeFactory;
import com.byco.remotejdbc.decode.resultrow.DefaultResultRow;
import com.byco.remotejdbc.decode.resultrow.ResultRow;

import com.byco.remotejdbc.metadata.DefaultResultSetMetaDataDecoder;
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
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Semaphore;

/**
 * @Classname QuerrySession
 * @Description TODO
 * @Date 2022/7/14 13:23
 * @Created by byco
 */
public class ClientSession {
    private String url;

    private ManagedChannel channel;
    private SimpleStatementGrpc.SimpleStatementStub stub;
    private StreamObserver<SimpleStatementRequest> requestObserver;
    private StreamObserver<SimpleStatementResponse> responseObserver;

    private Semaphore semaphore;


    private boolean canceled;
    private boolean closed;
    private boolean initialized;
    private Properties properties;

    private int bufferCapacity;


    private ResultRow buffer;



    private String queryBody;
    private SimpleStatementResponse response;

    private ResultSetMetaData metaData;
    private ResultRowDecodeFactory decodeFactory;
    private boolean remoteHasNext;

    public void connect(){
        channel =  ManagedChannelBuilder.forTarget(url)
            .usePlaintext()
            .build();
        stub = SimpleStatementGrpc.newStub(channel).withCompression("gzip");
        //TODO
        getResponseObserver();
        requestObserver = stub.exec(responseObserver);
        semaphore = new Semaphore(1);
        remoteHasNext = true;

    }

    public void setQueryBody(String queryBody) {
        this.queryBody = queryBody;
    }



    public static void main(String[] args) {
        ClientSession  session = new  ClientSession.Builder()
            .setUrl("localhost:9000")
            .setBufferCapacity(600).build();
        session.queryBody = "SELECT * from film";
        session.connect();
        session.requestInitialize();
        session.requestSendStatement();
        //session.requestReceiveData();
//        System.out.println(session.hasNext());
//        System.out.println(Arrays.toString(session.get()));
        while(session.hasNext()){
            System.out.println(Arrays.toString(session.get()));
        }
//        session.requestFinish();
//        session.requestObserver.onCompleted();


//        session = new  ClientSession.Builder()
//            .setUrl("localhost:9000")
//            .setBufferCapacity(600).build();
        session.queryBody = "SELECT * from actor";
        //session.connect();
        session.semaphore = new Semaphore(1);
        session.remoteHasNext = true;
        //session.requestInitialize();
        session.requestSendStatement();
        //session.requestReceiveData();
//        System.out.println(session.hasNext());
//        System.out.println(Arrays.toString(session.get()));
        while(session.hasNext()){
            System.out.println(Arrays.toString(session.get()));
        }
        session.requestFinish();
        session.requestObserver.onCompleted();

    }


    void acquire(){
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void release(){
        semaphore.release();
    }

    public boolean hasNext() {
        if(buffer.hasNext()){
            return true;
        }else{
            if( remoteHasNext ){
                requestReceiveData();
                acquire();
                release();
                return hasNext();
            }else{
                return false;
            }
        }
    }

    public boolean isEmpty() {
        return buffer.isEmpty();
    }

    public Object[] get() {
        return buffer.get();
    }


    private void requestInitialize()    {
        SimpleStatementRequest request = SimpleStatementRequest.newBuilder()
            .setStatus(ClientStatus.CLIENT_STATUS_INITIALIZE)
            .build();
        acquire();
        requestObserver.onNext(request);
    }

    private void requestSendStatement()   {
        SimpleStatementRequest request = SimpleStatementRequest.newBuilder()
            .setStatus(ClientStatus.CLIENT_STATUS_SEND_STATEMENT)
            .setBody(queryBody)
            .build();
        acquire();
        requestObserver.onNext(request);
    }



    private void requestReceiveData()  {
        SimpleStatementRequest request = SimpleStatementRequest.newBuilder()
            .setStatus(ClientStatus.CLIENT_STATUS_RECEIVE_DATA)
            .build();
        acquire();
        requestObserver.onNext(request);
    }

    private void requestFinish()  {
        SimpleStatementRequest request = SimpleStatementRequest.newBuilder()
            .setStatus(ClientStatus.CLIENT_STATUS_FINISHED)
            .build();
        acquire();
        requestObserver.onNext(request);
    }

    private void requestUnknown()   {
        SimpleStatementRequest request = SimpleStatementRequest.newBuilder()
            .setStatus(ClientStatus.CLIENT_STATUS_UNKNOWN)
            .build();
        acquire();
        requestObserver.onNext(request);
    }
    private void requestCancel()   {
        SimpleStatementRequest request = SimpleStatementRequest.newBuilder()
            .setStatus(ClientStatus.CLIENT_STATUS_CANCEL)
            .build();
        acquire();
        requestObserver.onNext(request);
    }
    private void requestError()   {
        SimpleStatementRequest request = SimpleStatementRequest.newBuilder()
            .setStatus(ClientStatus.CLIENT_STATUS_ERROR)
            .build();
        acquire();
        requestObserver.onNext(request);
    }

    private void responseUnknown(){
        System.out.println("unknownResponse");
    }

    private void responseInitialized() throws IOException, SQLException {

        initialized = true;
    }

    private void responseFinished(){
        closed = true;
    }
    private void responseError(){
        System.out.println("errorResponse");
    }
    private void responseReceivedStatement() throws IOException, InterruptedException,
        SQLException {
        ByteString metaDataRaw = response.getResult(0);
        metaData =  new DefaultResultSetMetaDataDecoder().decode(metaDataRaw.toByteArray());
        decodeFactory = new ResultRowDecodeFactory.Builder(metaData).build();
    }
    private void responseHasNextData() throws IOException, InterruptedException {
        List<ByteString> l = response.getResultList();
        for( ByteString b : l ){
            buffer.put( decodeFactory.read(b.toByteArray()) );
        }
        remoteHasNext = true;

    }
    private void responseNotHasNextData() throws IOException, InterruptedException {
        List<ByteString> l = response.getResultList();
        for( ByteString b : l ){
            buffer.put( decodeFactory.read(b.toByteArray()) );
        }
        remoteHasNext = false;

    }
    private void responseCanceled(){
        canceled = true;
    }


    private void doAction( SimpleStatementResponse response ) {
        this.response = response;
        ServerStatus action = response.getStatus();
       try{
        switch (action){
            case SERVER_STATUS_UNKNOWN: responseUnknown(); break;
            case SERVER_STATUS_INITIALIZED: responseInitialized();break;
            case SERVER_STATUS_FINISHED: responseFinished();break;
            case SERVER_STATUS_ERROR: responseError();break;
            case SERVER_STATUS_RECEIVED_STATEMENT: responseReceivedStatement();break;
            case SERVER_STATUS_HAS_NEXT_DATA: responseHasNextData();break;
            case SERVER_STATUS_NOT_HAS_NEXT_DATA: responseNotHasNextData();break;
            case SERVER_STATUS_CANCELED: responseCanceled();break;
        }
       }catch ( SQLException | IOException | InterruptedException e){
            throw new RuntimeException(e);
       }

    }



    private void getResponseObserver(){
        responseObserver = new StreamObserver<SimpleStatementResponse>() {
            @Override
            public void onNext(SimpleStatementResponse value) {
                doAction(value);
                release();
            }
            @Override
            public void onError(Throwable t) {
                release();
                System.out.println("onError" + t.getMessage());
                t.printStackTrace();
            }

            @Override
            public void onCompleted() {
                release();
                System.out.println("onCompleted");
            }
        };
    }




    private ClientSession(){

    }




    public static class Builder{

        private static final int DEFAULT_BUFFER_CAPACITY = 500;

        private String url;
        private int bufferCapacity;
        private Properties properties;
        private ResultRow resultRow;
        public Builder(){

        }

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setBufferCapacity(int bufferCapacity) {
            this.bufferCapacity = bufferCapacity;
            return this;
        }

        public Builder setProperties(Properties properties) {
            this.properties = properties;
            return this;
        }

        public void check(){
            if(Strings.isNullOrEmpty(url)) throw new IllegalArgumentException("url is empty");
        }

        public ClientSession build(){
            if( bufferCapacity == 0 ) bufferCapacity = DEFAULT_BUFFER_CAPACITY;
            if( resultRow == null ) {
                resultRow = new DefaultResultRow(bufferCapacity);
            }
            ClientSession session = new ClientSession();
            session.properties = this.properties;
            session.url = this.url;
            session.bufferCapacity = this.bufferCapacity;
            session.buffer = resultRow;
            return session;
        }
    }
}