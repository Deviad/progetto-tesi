package io.deviad.ripeti.webapp.domain.valueobject.user;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Value;
import lombok.With;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
   This is a value object
*/

@Value(staticConstructor = "of")
@With
@Accessors(fluent = true)
/*
   The following two lines are used to tell Jackson that
   getters/setters are not standard with prefix get/set.
*/
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Getter
public class Address {
  private final String firstAddressLine;
  private final String secondAddressLine;
  private final String city;
  private final String country;

  public Address(String firstAddressLine, String secondAddressLine, String city, String country) {
    validate(firstAddressLine, city, country);
    validateSecondLine(secondAddressLine);
    this.firstAddressLine = firstAddressLine;
    this.secondAddressLine = secondAddressLine;
    this.city = city;
    this.country = country;
  }

  void validate(String... addressElements) {
    List.of(addressElements)
        .forEach(
            (el) -> {
              Pattern p = Pattern.compile("^[A-Za-z0-9,.]{3,50}$");
              Matcher m = p.matcher(el);
              if (!m.matches()) {
                throw new IllegalArgumentException("Invalid address");
              }
            });
  }

  void validateSecondLine(String addressLine) {
    Pattern p = Pattern.compile("^[A-Za-z0-9,.]{3,50}$");
    Matcher m = p.matcher(addressLine);
    if (!m.matches()) {
      throw new IllegalArgumentException("Invalid address");
    }
  }

  @Override
  public boolean equals(Object other) {

    if (!this.getClass().getName().equals(other.getClass().getName())) {
      return false;
    }

    Address that = (Address) other;

    return this.firstAddressLine().equals(that.firstAddressLine())
        && this.secondAddressLine().equals(that.secondAddressLine())
        && this.city().equals(that.city())
        && this.country().equals(that.country());
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result =
        prime * result
            + ((this.firstAddressLine() == null) ? 0 : this.firstAddressLine().hashCode());
    result =
        prime * result
            + ((this.secondAddressLine() == null) ? 0 : this.secondAddressLine().hashCode());
    result = prime * result + ((this.city() == null) ? 0 : this.city().hashCode());
    result = prime * result + ((this.country() == null) ? 0 : this.country().hashCode());
    return result;
  }
}
