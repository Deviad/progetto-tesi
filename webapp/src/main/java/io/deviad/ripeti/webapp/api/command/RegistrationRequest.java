package io.deviad.ripeti.webapp.api.command;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.deviad.ripeti.webapp.domain.valueobject.user.Address;
import io.deviad.ripeti.webapp.domain.valueobject.user.FirstName;
import io.deviad.ripeti.webapp.domain.valueobject.user.LastName;
import io.deviad.ripeti.webapp.domain.valueobject.user.Password;
import io.deviad.ripeti.webapp.domain.valueobject.user.Username;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
/*
    The following two lines are used to tell Jackson that
    getters/setters are not standard with prefix get/set.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record RegistrationRequest(Username username, Password password, FirstName firstName,
                                  LastName lastName, Address address) {
}
