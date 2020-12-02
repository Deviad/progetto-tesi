package io.deviad.ripeti.webapp.domain.event.command;

import io.deviad.ripeti.webapp.domain.valueobject.user.Username;
import lombok.Value;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.util.UUID;

@Value
@Accessors(fluent = true)
public class Remove implements Command {

    public static final String TYPE = "user.remove";

    private final UUID uuid;
    private final Instant when;
    private final Username username;


}
