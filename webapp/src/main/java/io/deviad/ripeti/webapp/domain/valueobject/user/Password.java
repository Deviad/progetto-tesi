package io.deviad.ripeti.webapp.domain.valueobject.user;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Value;
import lombok.With;
import lombok.experimental.Accessors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
   This is a value object
*/
@With
@Value
@Accessors(fluent = true)
/*
   The following two lines are used to tell Jackson that
   getters/setters are not standard with prefix get/set.
*/
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class Password {
  @JsonUnwrapped private final String password;

  public Password(String password) {
    validate(password);
    this.password = password;
  }

  public void validate(String password) {
    Pattern p =
        Pattern.compile(
            "(?=.*[a-z]+)(?=.*[0-9]+)(?=.*[A-Z]+)(?=.*[!@#$%^&*()_+\\[\\]{}:\";,.<>?|=-_]+).{8,20}");
    Matcher m = p.matcher(password);
    if (!m.matches()) {
      throw new IllegalArgumentException("Password cannot be accepted");
    }
  }

  @Override
  public boolean equals(Object other) {

    if (other == null) {
      return false;
    }

    if (!this.getClass().getName().equals(other.getClass().getName())) {
      return false;
    }

    Password that = (Password) other;

    return this.password().equals(that.password());
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((password == null) ? 0 : password.hashCode());
    return result;
  }
}
