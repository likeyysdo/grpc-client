package com.byco.remotejdbc.decode.statement;

import com.byco.remotejdbc.rpc.HelloWorldClient;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import io.quarkus.remote.SimpleStatementGrpc;
import io.quarkus.remote.SimpleStatementRequest;
import io.quarkus.remote.SimpleStatementResponse;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @Classname QuerrySession
 * @Description TODO
 * @Date 2022/7/14 13:23
 * @Created by byco
 */
public class QuerySession {

    public void execute(){

    }

    public static void main(String[] args) throws InterruptedException {
        String target = "localhost:9000";
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
            // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
            // needing certificates.
            .usePlaintext()
            .build();
        SimpleStatementGrpc.SimpleStatementStub stub = SimpleStatementGrpc.newStub(channel);
        SimpleStatementRequest request = SimpleStatementRequest.newBuilder().setBody("hahaha").build();
        StreamObserver<SimpleStatementRequest> requestObserver = stub.exec(new StreamObserver<SimpleStatementResponse>() {
            @Override
            public void onNext(SimpleStatementResponse simpleStatementResponse) {
                System.out.println("Incoming Message");
                System.out.println(simpleStatementResponse.getStatus().name());
                System.out.println(simpleStatementResponse.getBody());
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
        requestObserver.onNext(request);
        requestObserver.onNext(request);
        requestObserver.onNext(request);

        requestObserver.onCompleted();
        //Thread.sleep(3000);
        countDownLatch.await(1,TimeUnit.MINUTES);
    }
}
