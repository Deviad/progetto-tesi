package io.deviad.ripeti.webapp.domain.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.deviad.ripeti.webapp.domain.event.user.UserRegistered;
import io.deviad.ripeti.webapp.domain.event.user.UserRemoved;
import io.deviad.ripeti.webapp.domain.event.user.UserUpdated;

import java.time.Instant;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(name = UserRegistered.TYPE, value = UserRegistered.class),
  @JsonSubTypes.Type(name = UserUpdated.TYPE, value = UserUpdated.class),
  @JsonSubTypes.Type(name = UserRemoved.TYPE, value = UserRemoved.class),
})
public interface DomainEvent {

  String type();

  Instant when();

  Object payload();
}
