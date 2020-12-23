package io.deviad.ripeti.webapp.persistence;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.deviad.ripeti.webapp.domain.valueobject.user.FirstName;
import io.deviad.ripeti.webapp.domain.valueobject.user.LastName;
import io.deviad.ripeti.webapp.domain.valueobject.user.Password;
import io.deviad.ripeti.webapp.domain.valueobject.user.Username;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
  @Id Long id;

  @Column("username")
  @JsonUnwrapped
  Username username;

  @Column("password")
  @JsonUnwrapped
  Password password;

  @Column("first_name")
  @JsonUnwrapped
  FirstName firstName;

  @Column("last_name")
  @JsonUnwrapped
  LastName lastName;

  @Column("address_id")
  Long addressId;
}
