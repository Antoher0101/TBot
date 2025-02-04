package com.mawus.core.grpc.server.service;

import com.google.protobuf.Empty;
import com.google.protobuf.Timestamp;
import com.mawus.core.entity.Client;
import com.mawus.core.grpc.ClientServiceGrpc;
import com.mawus.core.grpc.ClientServiceProto;
import com.mawus.core.grpc.model.ClientProto;
import com.mawus.core.service.ClientService;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service("grpcClientService")
public class ClientServiceImpl extends ClientServiceGrpc.ClientServiceImplBase {

    private final ClientService clientService;

    public ClientServiceImpl(ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public void findAll(Empty request, StreamObserver<ClientServiceProto.AllClientsResponse> responseObserver) {
        List<Client> clients = clientService.findAll();

        ClientServiceProto.AllClientsResponse.Builder responseBuilder = ClientServiceProto.AllClientsResponse.newBuilder();
        for (Client client : clients) {
            ClientProto.Client clientProto = buildClient(client);
            responseBuilder.addClients(clientProto);
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void findById(ClientServiceProto.FindByIdRequest request, StreamObserver<ClientServiceProto.ClientResponse> responseObserver) {
        Client client = clientService.findById(UUID.fromString(request.getId()));

        ClientProto.Client clientProto = buildClient(client);

        responseObserver.onNext(ClientServiceProto.ClientResponse.newBuilder().setClient(clientProto).build());
        responseObserver.onCompleted();
    }

    @Override
    public void count(Empty request, StreamObserver<ClientServiceProto.CountResponse> responseObserver) {
        Long count = clientService.count();

        ClientServiceProto.CountResponse response = ClientServiceProto.CountResponse.newBuilder()
                .setCount(count)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void activeCount(Empty request, StreamObserver<ClientServiceProto.CountResponse> responseObserver) {
        Long count = clientService.countActive();

        ClientServiceProto.CountResponse response = ClientServiceProto.CountResponse.newBuilder()
                .setCount(count)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private ClientProto.Client buildClient(Client client) {
        Instant instant = client.getCreateTs().toInstant(ZoneOffset.UTC);
        return ClientProto.Client.newBuilder()
                .setChatId(client.getChatId())
                .setId(client.getId().toString())
                .setName(client.getName() != null ? client.getName() : "")
                .setPhoneNumber(client.getPhoneNumber() != null ? client.getPhoneNumber() : "")
                .setCreateTs(Timestamp.newBuilder()
                        .setSeconds(instant.getEpochSecond())
                        .setNanos(instant.getNano())
                        .build())
                .setActive(client.isActive())
                .build();
    }
}
