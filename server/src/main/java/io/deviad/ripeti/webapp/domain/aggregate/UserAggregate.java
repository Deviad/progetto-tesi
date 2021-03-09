package io.deviad.ripeti.webapp.domain.aggregate;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.deviad.ripeti.webapp.domain.valueobject.user.Address;
import io.deviad.ripeti.webapp.domain.valueobject.user.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.With;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Accessors(fluent = true)
@Table("users")
@Data
@With
@Builder
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class UserAggregate {
  @Id UUID id;

  @Column("username")
  String username;

  @Column("password")
  String password;

  @Column("email")
  String email;

  @Column("first_name")
  String firstName;

  @Column("last_name")
  String lastName;

  @Column("address")
  Address address;

  @Column("role")
  Role role;
}
