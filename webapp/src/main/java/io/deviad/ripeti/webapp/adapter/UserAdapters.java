package io.deviad.ripeti.webapp.adapter;

import io.deviad.ripeti.webapp.api.queries.UserInfoDto;
import io.deviad.ripeti.webapp.domain.valueobject.user.Address;
import io.deviad.ripeti.webapp.domain.valueobject.user.Role;
import io.deviad.ripeti.webapp.domain.aggregate.UserAggregate;
import io.r2dbc.spi.Row;

import java.util.Arrays;
import java.util.function.BiFunction;

public class UserAdapters {

  public static BiFunction<Row, Object, UserInfoDto> USERINFO_FROM_ROW_MAP =
      (Row row, Object o) -> {
        var username = row.get("username", String.class);
        var firstName = row.get("first_name", String.class);
        var email = row.get("email", String.class);
        var role = row.get("role", Role.class);
        var lastName = row.get("last_name", String.class);
        var address = Arrays
                .stream(row.get("address", String.class).split("\\|"))
                .map(x-> x.replace(" \\", "")).toArray(String[]::new);
        var address_obj =
            Address.builder()
                .firstAddressLine(address[0])
                .secondAddressLine(address[1])
                .city(address[2])
                .country(address[3])
                .build();
        return UserInfoDto.of(username, email, firstName, lastName, role, address_obj);
      };

  public static UserInfoDto mapToUserInfo(UserAggregate u) {
    return UserInfoDto.of(
        u.username(),
        u.email(),
        u.firstName(),
        u.lastName(),
        u.role(),
        Address.builder()
            .firstAddressLine(u.address().firstAddressLine())
            .secondAddressLine(u.address().secondAddressLine())
            .city(u.address().city())
            .country(u.address().country())
            .build());
  }
}
