package io.deviad.ripeti.webapp.domain.event.user;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.deviad.ripeti.webapp.domain.event.DomainEvent;
import io.deviad.ripeti.webapp.domain.valueobject.user.Address;
import io.deviad.ripeti.webapp.domain.valueobject.user.FirstName;
import io.deviad.ripeti.webapp.domain.valueobject.user.LastName;
import io.deviad.ripeti.webapp.domain.valueobject.user.Password;
import io.deviad.ripeti.webapp.domain.valueobject.user.Username;
import lombok.experimental.Accessors;

import java.time.Instant;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Accessors(fluent = true)
public record UserRegistered(Instant when, UserRegisteredPayload payload) implements DomainEvent {
    public static final String TYPE = "user.registered";

    @Override
    public String type() {
        return TYPE;
    }

    static record UserRegisteredPayload(Username username,
                                        Password password,
                                        Address address,
                                        FirstName firstName,
                                        LastName lastName) {
    }

}
