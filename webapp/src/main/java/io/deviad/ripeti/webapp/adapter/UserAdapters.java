package io.deviad.ripeti.webapp.adapter;

import io.deviad.ripeti.webapp.api.dto.Address;
import io.deviad.ripeti.webapp.api.dto.UserInfo;
import io.deviad.ripeti.webapp.persistence.AddressEntity;
import io.deviad.ripeti.webapp.persistence.UserEntity;
import io.r2dbc.spi.Row;
import reactor.util.function.Tuple2;

import java.util.function.BiFunction;

public class UserAdapters {

  public static BiFunction<Row, Object, UserInfo> USERINFO_FROM_ROW_MAP =
      (Row row, Object o) -> {
        var username = row.get("username", String.class);
        var firstName = row.get("first_name", String.class);
        var email = row.get("email", String.class);
        var lastName = row.get("last_name", String.class);
        var firstAddressLine = row.get("first_address_line", String.class);
        var secondAddressLine = row.get("second_address_line", String.class);
        var city = row.get("city", String.class);
        var country = row.get("country", String.class);
        var address =
            Address.builder()
                .firstAddressLine(firstAddressLine)
                .secondAddressLine(secondAddressLine)
                .city(city)
                .country(country)
                .build();
        return UserInfo.of(username, email, firstName, lastName, address);
      };

  public static UserInfo mapToUserInfo(Tuple2<UserEntity, AddressEntity> t) {
    return UserInfo.of(
        t.getT1().username(),
        t.getT1().email(),
        t.getT1().firstName(),
        t.getT1().lastName(),
        Address.builder()
            .firstAddressLine(t.getT2().getFirstAddressLine())
            .secondAddressLine(t.getT2().getSecondAddressLine())
            .city(t.getT2().getCity())
            .country(t.getT2().getCountry())
            .build());
  }
}
