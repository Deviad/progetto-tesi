package io.deviad.ripeti.webapp.api.command;

import io.deviad.ripeti.webapp.domain.valueobject.user.Address;
import io.deviad.ripeti.webapp.domain.valueobject.user.FirstName;
import io.deviad.ripeti.webapp.domain.valueobject.user.LastName;
import io.deviad.ripeti.webapp.domain.valueobject.user.Password;
import io.deviad.ripeti.webapp.domain.valueobject.user.Username;

public record RegistrationRequest(Username username,
                                  Password password,
                                  FirstName firstName,
                                  LastName lastName,
                                  Address address) {
}
