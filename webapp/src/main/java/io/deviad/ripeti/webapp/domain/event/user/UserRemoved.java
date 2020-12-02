package io.deviad.ripeti.webapp.domain.event.user;

import io.deviad.ripeti.webapp.domain.event.DomainEvent;
import io.deviad.ripeti.webapp.domain.valueobject.user.Username;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRemoved implements DomainEvent {
    public static final String TYPE = "user.removed";

    private UUID uuid;
    private Instant when;
    private Username username;


    @Override
    public String type() {
        return TYPE;
    }

    @Override
    public Instant when() {
        return when;
    }

    @Override
    public UUID aggregateUuid() {
        return uuid;
    }


}
