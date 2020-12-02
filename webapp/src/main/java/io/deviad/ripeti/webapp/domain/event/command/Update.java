package io.deviad.ripeti.webapp.domain.event.command;

import io.deviad.ripeti.webapp.domain.valueobject.user.Address;
import io.deviad.ripeti.webapp.domain.valueobject.user.FirstName;
import io.deviad.ripeti.webapp.domain.valueobject.user.LastName;
import io.deviad.ripeti.webapp.domain.valueobject.user.Password;
import lombok.Value;
import lombok.experimental.Accessors;

import java.time.Instant;
import java.util.UUID;

@Value
@Accessors(fluent = true)
public class Update implements Command {

    public static final String TYPE = "user.update";

    private final UUID uuid;
    private final Instant when;
    private final Password password;
    private final FirstName firstName;
    private final LastName lastName;
    private final Address address;


}
