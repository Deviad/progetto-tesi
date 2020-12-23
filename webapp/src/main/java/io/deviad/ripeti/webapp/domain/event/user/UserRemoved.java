package io.deviad.ripeti.webapp.domain.event.user;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.deviad.ripeti.webapp.domain.event.DomainEvent;
import io.deviad.ripeti.webapp.domain.valueobject.user.Username;
import lombok.experimental.Accessors;

import java.time.Instant;


/*
    The following two lines are used to tell Jackson that
    getters/setters are not standard with prefix get/set.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Accessors(fluent = true)
public record UserRemoved(Instant when, UserRemovedPayload payload) implements DomainEvent {
    public static final String TYPE = "user.removed";

    @Override
    public String type() {
        return TYPE;
    }

    public static record UserRemovedPayload(Username username) {

    }

}

