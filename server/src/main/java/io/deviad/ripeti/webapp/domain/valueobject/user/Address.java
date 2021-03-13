package io.deviad.ripeti.webapp.domain.valueobject.user;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Accessors(fluent = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Value
@Builder
public class Address {
  @NotBlank
  @Length(min = 3, max = 100)
  String firstAddressLine;

  @NotBlank
  @Length(min = 3, max = 100)
  String secondAddressLine;

  @NotBlank
  @Length(min = 3, max = 20)
  @Pattern(regexp = "^[A-Za-z ]+$")
  String city;

  @NotBlank
  @Length(min = 3, max = 20)
  @Pattern(regexp = "^[A-Za-z ]+$")
  String country;
}
