package com.mawus.core.config;

import com.mawus.core.app.grpc.GrpcServer;
import com.mawus.core.app.grpc.GrpcServerProperties;
import io.grpc.BindableService;
import io.grpc.protobuf.services.ProtoReflectionService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConditionalOnProperty(name = "core.grpc.server.enabled", havingValue = "true")
@EnableConfigurationProperties(GrpcServerProperties.class)
public class GrpcConfig {

    @Bean
    public GrpcServer grpcServer(
            List<BindableService> services,
            GrpcServerProperties properties) {
        if (properties.isReflectionEnabled()) {
            services.add(ProtoReflectionService.newInstance());
        }

        return new GrpcServer(properties.getPort(), services);
    }
}
