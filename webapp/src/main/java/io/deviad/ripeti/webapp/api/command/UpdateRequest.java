package io.deviad.ripeti.webapp.api.command;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.deviad.ripeti.webapp.domain.valueobject.user.Address;
import io.deviad.ripeti.webapp.domain.valueobject.user.FirstName;
import io.deviad.ripeti.webapp.domain.valueobject.user.LastName;
import io.deviad.ripeti.webapp.domain.valueobject.user.Password;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record UpdateRequest(Password password, FirstName firstName, LastName lastname, Address address) {

}
