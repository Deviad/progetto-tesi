package io.deviad.ripeti.webapp.eventstore;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@Document(indexName = "event_streams")
class EventStream {

    @Id
    private Long id;

    @Getter
//    @Column(unique = true, nullable = false, name = "aggregate_uuid", length = 36)
    @Field
    private UUID aggregateUUID;

    @Version
//    @Column(nullable = false)
    @Field(type = FieldType.Long)
    private Long version;

    @Field(type = FieldType.Nested, includeInParent = true)
    private List<EventDescriptor> events = new ArrayList<>();

    private EventStream() {
    }

    EventStream(UUID aggregateUUID) {
        this.aggregateUUID = aggregateUUID;
    }

    void addEvents(List<EventDescriptor> events) {
        this.events.addAll(events);
    }

    List<EventDescriptor> getEvents() {
        return events
                .stream()
                .sorted(comparing(EventDescriptor::getOccurredAt))
                .collect(toList());
    }

}
