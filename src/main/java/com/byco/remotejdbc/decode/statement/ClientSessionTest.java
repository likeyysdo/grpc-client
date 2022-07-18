package com.byco.remotejdbc.decode.statement;

import com.byco.remotejdbc.decode.ResultRowDecodeFactory;
import com.byco.remotejdbc.metadata.DefaultResultSetMetaDataDecoder;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @Classname QuerrySession
 * @Description TODO
 * @Date 2022/7/14 13:23
 * @Created by byco
 */
public class ClientSessionTest {

    public void execute(){

    }

    public static void main(String[] args) throws InterruptedException, IOException, SQLException {
        String target = "localhost:9000";
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final CyclicBarrier cyclicBarrier = new CyclicBarrier(1);
        Semaphore semaphore = new Semaphore(1);
        cyclicBarrier.reset();

        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
            // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
            // needing certificates.
            .usePlaintext()
            .build();
        SimpleStatementGrpc.SimpleStatementStub stub = SimpleStatementGrpc.newStub(channel);

        final ByteString[] i = {null};
        final ByteString[] r = new ByteString[4];
        StreamObserver<SimpleStatementRequest> requestObserver = stub.exec(new StreamObserver<SimpleStatementResponse>() {

            ResultSetMetaData decodeMeta;
            ResultRowDecodeFactory factory1 ;

            @Override
            public void onNext(SimpleStatementResponse simpleStatementResponse) {
                System.out.println("Incoming Message");
                System.out.println(simpleStatementResponse.getStatus().name());
                System.out.println(simpleStatementResponse.getBody());
                System.out.println(simpleStatementResponse.getResultList().size());
                if( simpleStatementResponse.getStatus() == ServerStatus.SERVER_STATUS_RECEIVED_STATEMENT ){
                    try {
                        decodeMeta = new DefaultResultSetMetaDataDecoder().decode(simpleStatementResponse.getResult(0).toByteArray());
                        System.out.println(decodeMeta);
                        factory1 = new ResultRowDecodeFactory.Builder(decodeMeta).build();
                    } catch (IOException | SQLException e) {
                        e.printStackTrace();
                    }
                }
                if( simpleStatementResponse.getStatus() == ServerStatus.SERVER_STATUS_NOT_HAS_NEXT_DATA ){
                    System.out.println("read data");
                    for( ByteString b : simpleStatementResponse.getResultList()){
                        try {
                            System.out.println(Arrays.toString(factory1.read(b.toByteArray())));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
                semaphore.release();
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("onError "  );
                countDownLatch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("onCompleted " );
                countDownLatch.countDown();
            }
        });
        SimpleStatementRequest request = SimpleStatementRequest.newBuilder()
                .setStatus(ClientStatus.CLIENT_STATUS_INITIALIZE)
            .build();
        semaphore.acquire();
        requestObserver.onNext(request);
        request = SimpleStatementRequest.newBuilder()
                .setStatus(ClientStatus.CLIENT_STATUS_SEND_STATEMENT)
                    .setBody("SELECT * from bundle")
                        .build();
        semaphore.acquire();
        requestObserver.onNext(request);
        request = SimpleStatementRequest.newBuilder()
            .setStatus(ClientStatus.CLIENT_STATUS_RECEIVE_DATA)
            .build();
        semaphore.acquire();
        requestObserver.onNext(request);

        requestObserver.onCompleted();
        countDownLatch.await(1,TimeUnit.MINUTES);
        System.out.println("//Decode");
//        ResultSetMetaData decodeMeta = new DefaultResultSetMetaDataDecoder().decode(i[0].toByteArray());
//        System.out.println(decodeMeta);
//        ResultRowDecodeFactory factory1 = new ResultRowDecodeFactory.Builder(decodeMeta).build();
//        System.out.println(Arrays.toString(factory1.read(r[0].toByteArray())));
//        for( ByteString b : r ){
//            System.out.println(Arrays.toString(factory1.read(b.toByteArray())));
//        }

        //Thread.sleep(3000);

    }
}
