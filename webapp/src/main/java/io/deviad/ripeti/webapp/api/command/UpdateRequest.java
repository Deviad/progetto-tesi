package io.deviad.ripeti.webapp.api.command;

import io.deviad.ripeti.webapp.domain.valueobject.user.Address;
import io.deviad.ripeti.webapp.domain.valueobject.user.FirstName;
import io.deviad.ripeti.webapp.domain.valueobject.user.Password;
import io.deviad.ripeti.webapp.domain.valueobject.user.Username;

public class UpdateRequest {
    Username username;
    Password password;
    Address address;
    FirstName firstName;

}
