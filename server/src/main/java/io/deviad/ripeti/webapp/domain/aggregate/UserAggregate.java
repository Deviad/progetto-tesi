package io.deviad.ripeti.webapp.domain.aggregate;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.deviad.ripeti.webapp.domain.valueobject.user.Address;
import io.deviad.ripeti.webapp.domain.valueobject.user.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.With;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Accessors(fluent = true)
@Table("users")
@Data
@With
@Builder
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class UserAggregate implements Persistable<UUID> {

  @Column("id")
  @Id
  UUID id;

  @Column("username")
  @NonNull
  String username;

  @Column("password")
  @NonNull
  String password;

  @Column("email")
  @NonNull
  String email;

  @Column("first_name")
  @NonNull
  String firstName;

  @Column("last_name")
  @NonNull
  String lastName;

  @Column("address")
  @NonNull
  Address address;

  @Column("role")
  @NonNull
  Role role;

  @Transient private boolean newUser;

  @Override
  public UUID getId() {
    return id();
  }

  @Override
  @Transient
  public boolean isNew() {
    return this.newUser || id == null;
  }

  public UserAggregate setAsNew() {
    this.newUser = true;
    return this;
  }
}
