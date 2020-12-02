package io.deviad.ripeti.webapp.eventstore;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;
import java.util.UUID;

@Document(indexName = "event_descriptors")
public class EventDescriptor {

    public enum Status {
        PENDING, SENT
    }

    @Id
//    @GeneratedValue(generator = "event_descriptors_seq", strategy = GenerationType.SEQUENCE)
//    @SequenceGenerator(name = "event_descriptors_seq", sequenceName = "event_descriptors_seq", allocationSize = 1)
    @Getter
    private Long id;

    @Getter
    @Field(type = FieldType.Text)
    private String body;

    @Getter
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSZZ")
    private Instant occurredAt;

    @Getter
    @Field(type = FieldType.Text)
    private String type;

    @Field(type = FieldType.Text)
    @Getter
    private Status status = Status.PENDING;

    @Getter
//    @Column(nullable = false, name = "aggregate_uuid", length = 36)
    private UUID aggregateUUID;

    EventDescriptor(String body, Instant occurredAt, String type, UUID aggregateUUID) {
        this.body = body;
        this.occurredAt = occurredAt;
        this.type = type;
        this.aggregateUUID = aggregateUUID;
    }

    private EventDescriptor() {
    }

    public EventDescriptor sent() {
        this.status = Status.SENT;
        return this;
    }
}
