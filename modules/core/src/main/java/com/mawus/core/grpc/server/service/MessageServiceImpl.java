package com.mawus.core.grpc.server.service;

import com.mawus.core.entity.Message;
import com.mawus.core.grpc.MessageServiceGrpc;
import com.mawus.core.grpc.MessageServiceProto;
import com.mawus.core.grpc.model.MessageProto;
import com.mawus.core.service.MessageService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service("grpcMessageService")
public class MessageServiceImpl extends MessageServiceGrpc.MessageServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(MessageServiceImpl.class);

    private final MessageService messageService;

    public MessageServiceImpl(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void getMessage(MessageServiceProto.GetMessageRequest request, StreamObserver<MessageServiceProto.MessageResponse> responseObserver) {
        try {
            String key = request.getKey();
            String message = messageService.getMessage(key);

            MessageServiceProto.MessageResponse response = MessageServiceProto.MessageResponse.newBuilder()
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription("Message not found: " + request.getKey()).asRuntimeException());
        }
    }

    @Override
    public void getAllMessages(MessageServiceProto.GetAllMessagesRequest request, StreamObserver<MessageServiceProto.AllMessagesResponse> responseObserver) {
        log.info("grpc: retrieving all messages");
        List<Message> messages = messageService.getAllMessages();

        MessageServiceProto.AllMessagesResponse.Builder responseBuilder = MessageServiceProto.AllMessagesResponse.newBuilder();
        for (Message message : messages) {
            MessageProto.Message messageProto = MessageProto.Message.newBuilder()
                    .setId(message.getId().toString())
                    .setKey(message.getKey())
                    .setText(message.getText())
                    .setDescription(message.getDescription() == null ? "" : message.getDescription())
                    .build();

            responseBuilder.addMessages(messageProto);
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void updateMessage(MessageServiceProto.UpdateMessageRequest request, StreamObserver<MessageServiceProto.UpdateMessageResponse> responseObserver) {
        log.info("grpc: updating message {}", request.getId());
        Message message = messageService.updateMessage(UUID.fromString(request.getId()), request.getNewMessage(), request.getDescription());
        try {
            MessageServiceProto.UpdateMessageResponse response = MessageServiceProto.UpdateMessageResponse.newBuilder()
                    .setSuccess(message != null)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void deleteMessage(MessageServiceProto.DeleteMessageRequest request, StreamObserver<MessageServiceProto.DeleteMessageResponse> responseObserver) {
        UUID id = UUID.fromString(request.getId());
        log.info("grpc: deleting message {}", id);
        messageService.deleteMessage(id);
        String message = messageService.getMessage(id);
        try {
            MessageServiceProto.DeleteMessageResponse response = MessageServiceProto.DeleteMessageResponse.newBuilder()
                .setSuccess(message == null)
                .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }
}
