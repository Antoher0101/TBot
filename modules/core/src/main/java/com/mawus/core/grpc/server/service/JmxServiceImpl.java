package com.mawus.core.grpc.server.service;

import com.google.protobuf.Empty;
import com.mawus.core.grpc.jmx.model.JmxMethodInfoProto;
import com.mawus.core.jmx.JmxService;
import com.mawus.core.grpc.jmx.JmxServiceGrpc;
import com.mawus.core.grpc.jmx.JmxServiceProto;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.core.MethodInfo;
import org.springframework.stereotype.Service;

import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.ObjectName;
import java.util.Map;
import java.util.Set;

@Service("grpcJmxService")
public class JmxServiceImpl extends JmxServiceGrpc.JmxServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(JmxServiceImpl.class);
    private final JmxService jmxService;

    public JmxServiceImpl(JmxService jmxService) {
        this.jmxService = jmxService;
    }

    @Override
    public void listMBeans(Empty request, StreamObserver<JmxServiceProto.ListMBeansResponse> responseObserver) {
        Set<ObjectName> mBeans = jmxService.getMBeans();
        JmxServiceProto.ListMBeansResponse response = JmxServiceProto.ListMBeansResponse.newBuilder()
                .addAllMbeans(mBeans.stream().map(ObjectName::toString).toList())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void invokeMethod(JmxServiceProto.InvokeMethodRequest request, StreamObserver<JmxServiceProto.InvokeMethodResponse> responseObserver) {
        try {
            Object result = jmxService.invoke(
                    request.getObjectName(),
                    request.getOperationName(),
                    request.getParamsList().toArray(),
                    request.getSignatureList().toArray(new String[0])
            );

            JmxServiceProto.InvokeMethodResponse response = JmxServiceProto.InvokeMethodResponse.newBuilder()
                    .setResult(result.toString())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error while invoking MBean: {}", request.getOperationName(), e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getMethods(JmxServiceProto.GetMethodsRequest request, StreamObserver<JmxServiceProto.GetMethodsResponse> responseObserver) {
        try {
            Set<MBeanOperationInfo> operations = jmxService.getOperations(request.getObjectName());
            JmxServiceProto.GetMethodsResponse response = JmxServiceProto.GetMethodsResponse.newBuilder()
                    .addAllMethods(operations.stream().map(op -> {
                        JmxMethodInfoProto.JmxMethodInfo.Builder methodInfo = JmxMethodInfoProto.JmxMethodInfo.newBuilder()
                                .setName(op.getName());
                        for (MBeanParameterInfo param : op.getSignature()) {
                            methodInfo.addParameterTypes(param.getType())
                                    .addParameterNames(param.getName());
                        }
                        return methodInfo.build();
                    }).toList())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error("Error while getting MBean operations: {}", request.getObjectName(), e);
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void getAttributes(JmxServiceProto.GetAttributesRequest request, StreamObserver<JmxServiceProto.GetAttributesResponse> responseObserver) {
        try {
            Map<String, String> attributes = jmxService.getAttributes(request.getObjectName());
            JmxServiceProto.GetAttributesResponse response = JmxServiceProto.GetAttributesResponse.newBuilder()
                    .putAllAttributes(attributes)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        }
    }
}
