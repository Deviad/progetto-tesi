package io.deviad.ripeti.webapp.domain.valueobject.user;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;
import lombok.Value;
import lombok.With;
import lombok.experimental.Accessors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
   This is a value object
*/

@Value
@With
@Getter
@Accessors(fluent = true)
/*
   The following two lines are used to tell Jackson that
   getters/setters are not standard with prefix get/set.
*/
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class LastName {

  @JsonUnwrapped private final String lastName;

  public LastName(String lastName) {
    validate(lastName);
    this.lastName = lastName;
  }

  public void validate(String lastName) {
    Pattern p = Pattern.compile("^[A-Za-z]{3,20}$");
    Matcher m = p.matcher(lastName);
    if (!m.matches()) {
      throw new IllegalArgumentException("Password cannot be accepted");
    }
  }

  @Override
  public boolean equals(Object other) {

    if (!this.getClass().getName().equals(other.getClass().getName())) {
      return false;
    }

    LastName that = (LastName) other;

    return this.lastName.equals(that.lastName());
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
    return result;
  }
}
