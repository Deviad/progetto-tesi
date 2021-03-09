package io.deviad.ripeti.webapp.application.query;

import io.deviad.ripeti.webapp.adapter.UserAdapters;
import io.deviad.ripeti.webapp.ui.queries.UserInfoDto;
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
    public Mono<UserInfoDto> getUserInfo(String username) {
        //language=PostgreSQL
        String query =
                """
                SELECT username, email, role, address, first_name, last_name
                FROM users as u
                WHERE u.username = $1
                """;

        return client.getDatabaseClient().sql(query)
                .bind("$1", username)
                .map(UserAdapters.USERINFO_FROM_ROW_MAP::apply)
                .first();
    }
}
