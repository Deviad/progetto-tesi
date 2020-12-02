package io.deviad.ripeti.webapp.domain.event.user;

import io.deviad.ripeti.webapp.domain.event.DomainEvent;
import io.deviad.ripeti.webapp.domain.valueobject.user.Address;
import io.deviad.ripeti.webapp.domain.valueobject.user.FirstName;
import io.deviad.ripeti.webapp.domain.valueobject.user.LastName;
import io.deviad.ripeti.webapp.domain.valueobject.user.Password;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdated implements DomainEvent {
    public static final String TYPE = "user.updated";

    private UUID uuid;
    private Instant when;
    private Password password;
    private Address address;
    private FirstName firstName;
    private LastName lastName;

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
