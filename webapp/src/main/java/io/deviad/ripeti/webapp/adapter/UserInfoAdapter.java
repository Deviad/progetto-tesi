package io.deviad.ripeti.webapp.adapter;

import io.deviad.ripeti.webapp.api.dto.UserInfo;
import io.deviad.ripeti.webapp.domain.valueobject.user.Address;
import io.r2dbc.spi.Row;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
@Lazy
public class UserInfoAdapter implements BiFunction<Row, Object, UserInfo> {
  @Override
  public UserInfo apply(Row row, Object o) {
    var username = row.get("username", String.class);
    var firstName = row.get("first_name", String.class);
    var lastName = row.get("last_name", String.class);
    var firstAddressLine = row.get("first_address_line", String.class);
    var secondAddressLine = row.get("second_address_line", String.class);
    var city = row.get("city", String.class);
    var country = row.get("country", String.class);
    var address = Address.of(firstAddressLine, secondAddressLine, city, country);
    return UserInfo.of(username, firstName, lastName, address);
  }
}
