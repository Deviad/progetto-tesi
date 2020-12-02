package io.deviad.ripeti.webapp.domain.event.command;

import io.deviad.ripeti.webapp.domain.valueobject.user.Address;
import io.deviad.ripeti.webapp.domain.valueobject.user.FirstName;
import io.deviad.ripeti.webapp.domain.valueobject.user.LastName;
import io.deviad.ripeti.webapp.domain.valueobject.user.Password;
import io.deviad.ripeti.webapp.domain.valueobject.user.Username;
import lombok.Value;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.util.UUID;

@Value
@Accessors(fluent = true)
public class Register implements Command {

    public static final String TYPE = "user.register";

    private final UUID uuid = UUID.randomUUID();
    private final Instant when = Instant.now();
    private final Username username;
    private final Password password;
    private final FirstName firstName;
    private final LastName lastName;
    private final Address address;

}
