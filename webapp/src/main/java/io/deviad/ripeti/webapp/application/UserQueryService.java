package io.deviad.ripeti.webapp.application;

import io.deviad.ripeti.webapp.adapter.UserAdapters;
import io.deviad.ripeti.webapp.api.dto.UserInfo;
import io.micrometer.core.annotation.Timed;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j

@Lazy
@AllArgsConstructor
public class UserQueryService {

    R2dbcEntityOperations client;

    @Timed("getUserInfo")
    public Mono<UserInfo> getUserInfo(String username) {

        String query =
                """
                        SELECT username, email, first_name, last_name, first_address_line, second_address_line, city, country
                        FROM users as u
                        JOIN addresses as a on a.id = u.address_id
                        WHERE u.username = $1
                        """;

        return client.getDatabaseClient().sql(query)
                .bind("$1", username)
                .map(UserAdapters.USERINFO_FROM_ROW_MAP::apply)
                .first();
    }


}
