package io.deviad.ripeti.webapp.eventstore.publisher;

import io.deviad.ripeti.webapp.eventstore.EventDescriptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class EventPublisher {

    private final PendingEventRepository pendingEventRepository;
    private final PublishChannel publishChannel;

    @Autowired
    public EventPublisher(PendingEventRepository pendingEventRepository, PublishChannel publishChannel) {
        this.pendingEventRepository = pendingEventRepository;
        this.publishChannel = publishChannel;
    }

    @Scheduled(fixedRate = 2000)
    public void publishPending() {
        pendingEventRepository.listPending().forEach(this::sendSafely);
    }

    private EventDescriptor sendSafely(EventDescriptor event) {
        final String body = event.getBody();
        try {
            log.info("about to send: {}", body);
            publishChannel.sendMessage().apply(new Message<>() {
                @Override
                public String getPayload() {
                    return body;
                }

                @Override
                public MessageHeaders getHeaders() {
                    return new MessageHeaders(Map.ofEntries(Map.entry("uuid", event.getAggregateUUID())));
                }
            });
            pendingEventRepository.save(event.sent());
            log.info("send: {}", body);
        } catch (Exception e) {
            log.error("cannot send {}", body, e);
        }
        return event;
    }

}

