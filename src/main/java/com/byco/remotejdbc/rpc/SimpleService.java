package com.byco.remotejdbc.rpc;

/**
 * @Classname SimpleService
 * @Description TODO
 * @Date 2022/7/5 13:26
 * @Created by byco
 */
public class SimpleService extends io.quarkus.remote.SimpleStatementGrpc.SimpleStatementImplBase {
    @Override
    public void exec(io.quarkus.remote.SimpleStatementRequest request,
                     io.grpc.stub.StreamObserver<io.quarkus.remote.SimpleStatementResponse> responseObserver) {

    }
}
