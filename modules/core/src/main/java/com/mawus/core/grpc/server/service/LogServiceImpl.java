package com.mawus.core.grpc.server.service;

import com.google.protobuf.Empty;
import com.mawus.core.grpc.LogServiceGrpc;
import com.mawus.core.grpc.LogServiceProto;
import com.mawus.core.service.LogService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Service("grpcLogService")
public class LogServiceImpl extends LogServiceGrpc.LogServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(LogServiceImpl.class);
    private LogService logService;

    public LogServiceImpl(LogService logService) {
        this.logService = logService;
    }

    @Override
    public void getLogFiles(Empty request, StreamObserver<LogServiceProto.LogFilesListResponse> responseObserver) {
        Set<String> logFiles = logService.getLogFiles();

        LogServiceProto.LogFilesListResponse.Builder responseBuilder = LogServiceProto.LogFilesListResponse.newBuilder();
        logFiles.forEach(responseBuilder::addFilename);

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getLogFileContent(LogServiceProto.LogFileRequest request, StreamObserver<LogServiceProto.LogFileContent> responseObserver) {
        List<String> logFileContent = logService.getLogFileContent(request.getFilename());

        LogServiceProto.LogFileContent.Builder responseBuilder = LogServiceProto.LogFileContent.newBuilder();
        logFileContent.forEach(responseBuilder::addLines);

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void streamLogFileContent(LogServiceProto.LogFileRequest request, StreamObserver<LogServiceProto.LogLineResponse> responseObserver) {
        String fileName = request.getFilename();
        if (fileName.isBlank()) {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription("Filename cannot be null or blank.")
                            .asRuntimeException()
            );
            return;
        }
        try {
            Stream<String> logFileContent = logService.getLogFileContentAsStream(fileName);
            List<String> batch = new ArrayList<>();
            logFileContent.forEach(line -> {
                batch.add(line);
                if (batch.size() == 100) {
                    LogServiceProto.LogLineResponse response = LogServiceProto.LogLineResponse.newBuilder()
                            .setLine(String.join("\n", batch))
                            .build();
                    batch.clear();
                    try {
                        responseObserver.onNext(response);
                    } catch (Exception e) {
                        logFileContent.close();
                        throw new RuntimeException("Streaming interrupted: " + e.getMessage(), e);
                    }
                }
            });
            if (!batch.isEmpty()) {
                responseObserver.onNext(LogServiceProto.LogLineResponse.newBuilder()
                        .setLine(String.join("\n", batch))
                        .build());
            }
            responseObserver.onCompleted();

        } catch (UncheckedIOException e) {
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Error reading the file: " + fileName)
                            .asRuntimeException()
            );
        } catch (Exception e) {
            responseObserver.onError(
                    Status.UNKNOWN
                            .withDescription("An unexpected error occurred: " + e.getMessage())
                            .asRuntimeException()
            );
        }
    }
}
