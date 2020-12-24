package io.deviad.ripeti.webapp.persistence;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserEntity {
  @JsonIgnore
  @Id Long id;
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
