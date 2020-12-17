package io.deviad.ripeti.webapp.eventstore.publisher;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Function;

@Configuration
public class PublishChannel {

    @Bean
    public Function<Message<String>, String> sendMessage() {
        return Message::getPayload;
    }
}

