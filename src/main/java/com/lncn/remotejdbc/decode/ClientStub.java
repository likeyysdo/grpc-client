package com.lncn.remotejdbc.decode;

import com.lncn.remotejdbc.decode.resultset.resultrow.ResultRowDecodeFactory;
import com.lncn.remotejdbc.decode.resultset.resultrow.DefaultResultRow;
import com.lncn.remotejdbc.decode.resultset.resultrow.ResultRow;
import com.lncn.remotejdbc.decode.resultset.metadata.DefaultResultSetMetaDataDecoder;
import com.lncn.remotejdbc.utils.Log;
import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
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
import java.util.concurrent.Semaphore;

/**
 * @Classname ClientChannel
 * @Description TODO
 * @Date 2022/7/19 16:33
 * @Created by byco
 */
public class ClientStub {

    private static final Log log = new Log(ClientStub.class);

    private static final int DEFAULT_BUFFER_CAPACITY = 500;
    private ClientChannel clientChannel;
    private ManagedChannel channel;
    private SimpleStatementGrpc.SimpleStatementStub stub;
    private StreamObserver<SimpleStatementRequest> requestObserver;
    private StreamObserver<SimpleStatementResponse> responseObserver;
    private final Semaphore semaphore;
    private boolean canceled;
    private boolean closed;
    private boolean initialized;
    private ResultRow buffer;
    private String queryBody;
    private SimpleStatementResponse response;
    public ResultSetMetaData getMetaData() {
        return metaData;
    }
    private ResultSetMetaData metaData;
    private ResultRowDecodeFactory decodeFactory;
    private boolean remoteHasNext;
    private boolean error;
    private String errorMessage;


    public ClientStub(ClientChannel c) {
        log.debug("new ClientStub");
        clientChannel = c;
        channel = c.getChannel();
        buffer = new DefaultResultRow(c.getBufferCapacity());
        stub = SimpleStatementGrpc.newStub(channel).withCompression("gzip");
        getResponseObserver();
        requestObserver = stub.exec(responseObserver);
        semaphore = new Semaphore(1);
        remoteHasNext = true;
        requestInitialize();
    }

    public static void main(String[] args) throws SQLException {
        ClientChannel channel = new ClientChannel("localhost:9000",null);

        Thread t = new Thread() {
            @Override
            public void run() {
                ClientStub stub2 = channel.getStub();
                try {
                    stub2.query("SELECT * from address");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                while(true){
                    try {
                        if (!stub2.hasNext()) break;
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    System.out.println(Arrays.toString(stub2.get()));
                }
            }
        };
        t.start();
        Thread t1 = new Thread(() -> {
            ClientStub stub1 = channel.getStub();
            try {
                stub1.query("SELECT * from address");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            while(true){
                try {
                    if (!stub1.hasNext()) break;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println(Arrays.toString(stub1.get()));
            }
        });
        t1.start();
    }


    public void checkError(){

    }

    public void query(String queryBody) throws SQLException {
        log.debug("ClientStub start query",queryBody);
        this.queryBody = queryBody;
        awaitResponse();
        requestSendStatement();
        awaitResponse();
    }

    public void cancel(){
        log.debug("ClientStub cancel");
        if( canceled ) return;
        requestCancel();
        canceled = true;
    }

    public void close(){
        log.debug("ClientStub close");
        if( closed ) return;
        requestCancel();
        requestFinishNow();
        requestObserver.onCompleted();
        closed = true;
    }

    public boolean isEmpty() {
        return buffer.isEmpty();
    }

    public Object[] get() {
        return buffer.get();
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

    void awaitResponse() throws SQLException {
        acquire();
        release();
        if(error) throw new RJdbcSQLException(errorMessage);
    }

    public boolean hasNext() throws SQLException {
        if(buffer.hasNext()){
            return true;
        }else{
            if( remoteHasNext ){
                requestReceiveData();
                awaitResponse();
                return hasNext();
            }else{
                return false;
            }
        }
    }



    private void requestInitialize()    {
        log.debug("send Initialize",ClientStatus.CLIENT_STATUS_INITIALIZE );
        SimpleStatementRequest request;
        SimpleStatementRequest.Builder requestBuilder = SimpleStatementRequest.newBuilder()
            .setStatus(ClientStatus.CLIENT_STATUS_INITIALIZE);
        if( this.clientChannel.getProperties().isEmpty() ){
            request = requestBuilder.build();
        }else{
            request = requestBuilder.setBody(this.clientChannel.getEncodeProperties())
                .build();
        }
        acquire();
        requestObserver.onNext(request);
    }

    private void requestSendStatement()   {
        log.debug("send SendStatement",ClientStatus.CLIENT_STATUS_SEND_STATEMENT );
        SimpleStatementRequest request = SimpleStatementRequest.newBuilder()
            .setStatus(ClientStatus.CLIENT_STATUS_SEND_STATEMENT)
            .setBody(queryBody)
            .build();
        acquire();
        requestObserver.onNext(request);
    }



    private void requestReceiveData()  {
        log.debug("send ReceiveData",ClientStatus.CLIENT_STATUS_RECEIVE_DATA );
        SimpleStatementRequest request = SimpleStatementRequest.newBuilder()
            .setStatus(ClientStatus.CLIENT_STATUS_RECEIVE_DATA)
            .build();
        acquire();
        requestObserver.onNext(request);
    }

    private void requestFinish()  {
        log.debug("send Finish",ClientStatus.CLIENT_STATUS_FINISHED );
        SimpleStatementRequest request = SimpleStatementRequest.newBuilder()
            .setStatus(ClientStatus.CLIENT_STATUS_FINISHED)
            .build();
        acquire();
        requestObserver.onNext(request);
    }

    private void requestFinishNow()  {
        log.debug("send Finish",ClientStatus.CLIENT_STATUS_FINISHED );
        SimpleStatementRequest request = SimpleStatementRequest.newBuilder()
            .setStatus(ClientStatus.CLIENT_STATUS_FINISHED)
            .build();
        requestObserver.onNext(request);
    }

    private void requestUnknown()   {
        log.debug("send Unknown",ClientStatus.CLIENT_STATUS_UNKNOWN );
        SimpleStatementRequest request = SimpleStatementRequest.newBuilder()
            .setStatus(ClientStatus.CLIENT_STATUS_UNKNOWN)
            .build();
        requestObserver.onNext(request);
    }
    private void requestCancel()   {
        log.debug("send Cancel",ClientStatus.CLIENT_STATUS_CANCEL );
        SimpleStatementRequest request = SimpleStatementRequest.newBuilder()
            .setStatus(ClientStatus.CLIENT_STATUS_CANCEL)
            .build();
        requestObserver.onNext(request);
    }
    private void requestError()   {
        log.debug("send Error",ClientStatus.CLIENT_STATUS_ERROR );
        SimpleStatementRequest request = SimpleStatementRequest.newBuilder()
            .setStatus(ClientStatus.CLIENT_STATUS_ERROR)
            .build();
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
        log.error("errorResponse");
        errorMessage = response.getBody();
        error = true;
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
        log.debug("Incoming Response",action);
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
            log.error(e);
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
                log.error("Response onError" , t);
                release();
                log.debugError(t);
            }

            @Override
            public void onCompleted() {
                log.debug("Response onCompleted");
                try{
                    requestObserver.onCompleted();
                    release();
                }catch (Exception e){
                    log.error("onCompleted Error" ,e);
                    log.debugError(e);
                }
            }
        };
    }







}
