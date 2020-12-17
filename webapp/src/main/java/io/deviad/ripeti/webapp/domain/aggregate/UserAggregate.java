package io.deviad.ripeti.webapp.domain.aggregate;

import com.google.common.collect.ImmutableList;
import io.deviad.ripeti.webapp.domain.event.DomainEvent;
import io.deviad.ripeti.webapp.domain.event.command.Register;
import io.deviad.ripeti.webapp.domain.event.command.Remove;
import io.deviad.ripeti.webapp.domain.event.command.Update;
import io.deviad.ripeti.webapp.domain.event.user.UserRegistered;
import io.deviad.ripeti.webapp.domain.event.user.UserRemoved;
import io.deviad.ripeti.webapp.domain.event.user.UserUpdated;
import io.deviad.ripeti.webapp.domain.valueobject.user.UserStatus;
import io.vavr.API;
import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.control.Try;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.With;

import java.util.List;
import java.util.UUID;

import static io.deviad.ripeti.webapp.domain.valueobject.user.UserStatus.*;
import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;
import static io.vavr.collection.List.ofAll;
import static io.vavr.control.Try.of;

@RequiredArgsConstructor
@Getter
@With
public class UserAggregate {

    /**
     * State transitions -> MUST accept the facts and CANNOT contain any behaviour! (Event handlers).
     * f(state, event) -> state
     */
    private static final Function2<UserAggregate, UserRegistered, UserAggregate> registered =
            (state, event) -> state
                    .withUuid(event.getUuid())
                    .withStatus(REGISTERED);

    private static final Function2<UserAggregate, UserRemoved, UserAggregate> removed =
            (state, event) -> state
                    .withUuid(event.getUuid())
                    .withStatus(REMOVED);

    private static final Function2<UserAggregate, UserUpdated, UserAggregate> updated =
            (state, event) -> state
                    .withUuid(event.getUuid())
                    .withStatus(UPDATED);

    private static final Function2<UserAggregate, DomainEvent, UserAggregate> appendChange =
            (state, event) -> state
                    .patternMatch(event)
                    .withChanges(ImmutableList
                            .<DomainEvent>builder()
                            .addAll(state.changes)
                            .add(event)
                            .build());


    /**
     * Behaviour transitions -> Can fail or return new events
     * f(state, command) -> events
     */
    private static final Function2<UserAggregate, Register, DomainEvent> register =
            (state, command) ->
                    new UserRegistered(
                            state.uuid,
                            command.when(),
                            command.username(),
                            command.password(),
                            command.address(),
                            command.firstName(),
                            command.lastName());


    private static final Function2<UserAggregate, Remove, DomainEvent> remove =
            (state, command) ->
                    new UserRemoved(
                            state.uuid,
                            command.when(),
                            command.username());

    private static final Function2<UserAggregate, Update, DomainEvent> update =
            (state, command) ->
                    new UserUpdated(
                            state.uuid,
                            command.when(),
                            command.password(),
                            command.address(),
                            command.firstName(),
                            command.lastName());



    private static final Function1<UserAggregate, UserAggregate> noOp =
            (state) -> state;


    /**
     * low level details - state
     */
    private final UUID uuid;
    private final ImmutableList<DomainEvent> changes;
    private final UserStatus status;



    /**
     * Command Handlers
     */
    public Try<UserAggregate> register(Register command) {
        return of(() -> {
            if (status == INITIALIZED) {
                return appendChange.apply(this, register.apply(this, command));
            } else {
                return noOp.apply(this);
            }
        });
    }

    public Try<UserAggregate> remove(Remove command) {
        return of(() -> {
            throwIfStateIs(INITIALIZED, "Cannot remove not registered user");
            if (status != REMOVED) {
                return appendChange.apply(this, remove.apply(this, command));
            } else {
                return noOp.apply(this);
            }
        });
    }
    public Try<UserAggregate> update(Update command) {
        return of(() -> {
            throwIfStateIs(INITIALIZED, "Cannot remove not registered user");
            if (status != REMOVED) {
                return appendChange.apply(this, update.apply(this, command));
            } else {
                return noOp.apply(this);
            }
        });
    }

    private void throwIfStateIs(UserStatus unexpectedState, String msg) {
        if (status == unexpectedState) {
            throw new IllegalStateException(msg + (" UUID: " + uuid));
        }
    }


    /**
     * Rebuilding aggregate with left fold and pattern match
     * The result will be an object whose state is the result of the execution of
     * the patternMatch function having as input the state as it is in that moment and
     * the current element from the list.
     * The elements from the list are taken from left to right hence foldLeft
     */
    public static UserAggregate rebuild(UUID uuid, List<DomainEvent> history) {
        return ofAll(history)
                .foldLeft(
                        initialState(uuid),
                        UserAggregate::patternMatch);

    }

    private static UserAggregate initialState(UUID uuid) {
        return new UserAggregate(uuid, ImmutableList.of(), INITIALIZED);
    }


    private UserAggregate patternMatch(DomainEvent event) {
        return API.Match(event).of(
                Case($(instanceOf(UserRegistered.class)), this::registered),
                Case($(instanceOf(UserRemoved.class)), this::removed),
                Case($(instanceOf(UserUpdated.class)), this::updated)
        );
    }


    /**
     * Event handlers - must accept the fact
     */
    private UserAggregate registered(UserRegistered event) {
        return registered.apply(this, event);
    }

    private UserAggregate removed(UserRemoved event) {
        return removed.apply(this, event);
    }

    private UserAggregate updated(UserUpdated event) {
        return updated.apply(this, event);
    }


    /**
     * Getting and clearing all the changes (finishing the work with unit of work - aggregate)
     */

    public ImmutableList<DomainEvent> getUncommittedChanges() {
        return changes;
    }

    public UserAggregate markChangesAsCommitted() {
        return this.withChanges(ImmutableList.of());
    }
    
}
