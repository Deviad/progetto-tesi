package io.deviad.ripeti.webapp.domain.valueobject.user;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Accessors(fluent = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Value
@Builder
public class Address {
  @Pattern(regexp = "^[A-Za-z0-9,.]{3,50}$")
  @NotBlank
  String firstAddressLine;

  @Pattern(regexp = "^[A-Za-z0-9,.]{3,50}$")
  String secondAddressLine;

  @NotBlank
  @Pattern(regexp = "^[A-Za-z]{3,20}$")
  String city;

  @NotBlank
  @Pattern(regexp = "^[A-Za-z]{3,20}$")
  String country;
}
