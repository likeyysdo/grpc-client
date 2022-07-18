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

    private void initializeRequest() throws InterruptedException {
        SimpleStatementRequest request = SimpleStatementRequest.newBuilder()
            .setStatus(ClientStatus.CLIENT_STATUS_INITIALIZE)
            .build();
        semaphore.acquire();
        requestObserver.onNext(request);
    }

    private void sendStatementRequest() throws InterruptedException {
        SimpleStatementRequest request = SimpleStatementRequest.newBuilder()
            .setStatus(ClientStatus.CLIENT_STATUS_SEND_STATEMENT)
            .setBody(queryBody)
            .build();
        semaphore.acquire();
        requestObserver.onNext(request);
    }



    private void receiveDataRequest() throws InterruptedException {
        SimpleStatementRequest request = SimpleStatementRequest.newBuilder()
            .setStatus(ClientStatus.CLIENT_STATUS_RECEIVE_DATA)
            .build();
        semaphore.acquire();
        requestObserver.onNext(request);
    }

    private void finishRequest() throws InterruptedException {
        SimpleStatementRequest request = SimpleStatementRequest.newBuilder()
            .setStatus(ClientStatus.CLIENT_STATUS_FINISHED)
            .build();
        semaphore.acquire();
        requestObserver.onNext(request);
    }

    private void unknownRequest() throws InterruptedException {
        SimpleStatementRequest request = SimpleStatementRequest.newBuilder()
            .setStatus(ClientStatus.CLIENT_STATUS_UNKNOWN)
            .build();
        semaphore.acquire();
        requestObserver.onNext(request);
    }
    private void cancelRequest() throws InterruptedException {
        SimpleStatementRequest request = SimpleStatementRequest.newBuilder()
            .setStatus(ClientStatus.CLIENT_STATUS_CANCEL)
            .build();
        semaphore.acquire();
        requestObserver.onNext(request);
    }
    private void errorRequest() throws InterruptedException {
        SimpleStatementRequest request = SimpleStatementRequest.newBuilder()
            .setStatus(ClientStatus.CLIENT_STATUS_ERROR)
            .build();
        semaphore.acquire();
        requestObserver.onNext(request);
    }

    private void unknownResponse(){

    }

    private void initializedResponse() throws IOException, SQLException {
        ByteString metaDataRaw = response.getResult(0);
        metaData =  new DefaultResultSetMetaDataDecoder().decode(metaDataRaw.toByteArray());
        decodeFactory = new ResultRowDecodeFactory.Builder(metaData).build();
        initialized = true;
    }

    private void finishedResponse(){
        closed = true;
    }
    private void errorResponse(){

    }
    private void receivedStatementResponse() throws IOException, InterruptedException {

    }
    private void hasNextDataResponse() throws IOException, InterruptedException {
        List<ByteString> l = response.getResultList();
        for( ByteString b : l ){
            buffer.put( decodeFactory.read(b.toByteArray()) );
        }
    }
    private void notHasNextDataResponse() throws IOException, InterruptedException {
        List<ByteString> l = response.getResultList();
        for( ByteString b : l ){
            buffer.put( decodeFactory.read(b.toByteArray()) );
        }
    }
    private void canceledResponse(){

    }


    private void doAction( SimpleStatementResponse response ){
        this.response = response;
        ServerStatus action = response.getStatus();

        switch (action){
            case SERVER_STATUS_UNKNOWN:
            case SERVER_STATUS_INITIALIZED:
            case SERVER_STATUS_FINISHED:
            case SERVER_STATUS_ERROR:
            case SERVER_STATUS_RECEIVED_STATEMENT:
            case SERVER_STATUS_HAS_NEXT_DATA:
            case SERVER_STATUS_NOT_HAS_NEXT_DATA:
            case SERVER_STATUS_CANCELED:
        }

    }



    private void getResponseObserver(){
        responseObserver = new StreamObserver<SimpleStatementResponse>() {
            @Override
            public void onNext(SimpleStatementResponse value) {
                doAction(value);
                semaphore.release();
            }

            @Override
            public void onError(Throwable t) {
                semaphore.release();
            }

            @Override
            public void onCompleted() {
                semaphore.release();
            }
        };
    }


    private void connect(){
        channel =  ManagedChannelBuilder.forTarget(url)
            .usePlaintext()
            .build();
        stub = SimpleStatementGrpc.newStub(channel);
        //TODO
        getResponseObserver();
        requestObserver = stub.exec(responseObserver);
        semaphore = new Semaphore(1);
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
            return session;
        }
    }
}
