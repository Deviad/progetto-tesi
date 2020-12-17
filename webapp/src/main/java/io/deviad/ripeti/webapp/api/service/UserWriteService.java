package io.deviad.ripeti.webapp.api.service;

import io.deviad.ripeti.webapp.domain.event.command.Register;
import io.deviad.ripeti.webapp.domain.aggregate.UserAggregate;
import io.deviad.ripeti.webapp.domain.event.command.Remove;
import io.deviad.ripeti.webapp.domain.event.command.Update;
import io.deviad.ripeti.webapp.eventstore.EventSourcedCommandUserRepository;
import io.vavr.Function1;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
import java.util.function.Function;

@Service
@Slf4j
@Transactional
@Lazy
public class UserWriteService {

    private final EventSourcedCommandUserRepository userRepository;

    public UserWriteService(EventSourcedCommandUserRepository itemRepository) {
        this.userRepository = itemRepository;
    }

    public void register(Register command) {
        withUser(command.uuid(), user ->
                user.register(command)
        );
        log.info("{} user registered at {}", command.uuid(), command.when());
    }

    public void remove(Remove command) {
        withUser(command.uuid(), user ->
                user.remove(command)
        );
        log.info("{} user removed at {}", command.uuid(), command.when());
    }

    public void update(Update command) {
        withUser(command.uuid(), user ->
                user.update(command)
        );
        log.info("{} user updated at {}", command.uuid(), command.when());
    }



    public UserAggregate getByUUID(UUID uuid) {
        return userRepository.getByUUID(uuid);
    }

    private UserAggregate withUser(UUID uuid, Function1<UserAggregate, Try<UserAggregate>> action) {
        final UserAggregate user = getByUUID(uuid);
        final UserAggregate modified = action
                .apply(user)
                .getOrElseThrow((Function<Throwable, IllegalStateException>) IllegalStateException::new);
        return userRepository.save(modified);
    }

}
