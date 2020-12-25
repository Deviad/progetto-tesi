package io.deviad.ripeti.webapp.persistence;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Value;
import lombok.With;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Accessors(fluent = true)
@Table("users")
@Value
@With
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class UserEntity {
  @JsonIgnore
  @Id
  UUID id;
  @Column("username")
  String username;

  @Column("password")
  String password;

  @Column("first_name")
  String firstName;

  @Column("last_name")
  String lastName;

  @Column("address_id")
  @JsonIgnore
  Long addressId;
}
