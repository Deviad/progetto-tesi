package io.deviad.ripeti.webapp.domain.valueobject.user;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
/*
   The following two lines are used to tell Jackson that
   getters/setters are not standard with prefix get/set.
*/
public class Username {

  String username;

  @JsonCreator
  public Username(String username) {
    validate(username);
    this.username = username;
  }

  public void validate(String username) {

    if (username == null || username.equals(" ")) {
      throw new IllegalArgumentException("Username cannot be empty");
    }

    Pattern p = Pattern.compile("^[A-Za-z]{3,20}$");
    Matcher m = p.matcher(username);
    if (!m.matches()) {
      throw new IllegalArgumentException("Username cannot be accepted");
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

    Username that = (Username) other;

    return this.username().equals(that.username);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((username == null) ? 0 : username.hashCode());
    return result;
  }
}
