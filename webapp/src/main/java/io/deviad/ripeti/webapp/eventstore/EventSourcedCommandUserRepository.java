package io.deviad.ripeti.webapp.eventstore;


import io.deviad.ripeti.webapp.domain.GenericRepository;
import io.deviad.ripeti.webapp.domain.aggregate.UserAggregate;
import io.deviad.ripeti.webapp.domain.event.DomainEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Component
public class EventSourcedCommandUserRepository implements GenericRepository<UserAggregate> {

    private final EventStore eventStore;
    private final EventSerializer eventSerializer;

    @Autowired
    public EventSourcedCommandUserRepository(EventStore eventStore, EventSerializer eventSerializer) {
        this.eventStore = eventStore;
        this.eventSerializer = eventSerializer;
    }

    @Override
     public UserAggregate save(UserAggregate aggregate) {
        final List<DomainEvent> pendingEvents = aggregate.getUncommittedChanges();
        eventStore.saveEvents(
                aggregate.getUuid(),
                pendingEvents
                        .stream()
                        .map(eventSerializer::serialize)
                        .collect(toList()));
        return aggregate.markChangesAsCommitted();
    }

    @Override
    public UserAggregate getByUUID(UUID uuid) {
        return UserAggregate.rebuild(uuid, getRelatedEvents(uuid));
    }

    @Override
    public UserAggregate getByUUIDat(UUID uuid, Instant at) {
        return UserAggregate.
                rebuild(uuid,
                getRelatedEvents(uuid)
                        .stream()
                        .filter(evt -> !evt.when().isAfter(at))
                        .collect(toList()));
    }


    private List<DomainEvent> getRelatedEvents(UUID uuid) {
        return eventStore.getEventsForAggregate(uuid)
                .stream()
                .map(eventSerializer::deserialize)
                .collect(toList());
    }

}
