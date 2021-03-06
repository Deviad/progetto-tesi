package io.deviad.ripeti.webapp.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class OAuth2ResourceServerConfig {

  @Bean
  @Order(0)
  SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
    http.authorizeExchange()
        .pathMatchers("/swagger**", "/webjars/swagger-ui/**", "/v3/api-docs/**")
        .permitAll()
        .pathMatchers(HttpMethod.OPTIONS, "/api/**")
        .permitAll()
        .pathMatchers(HttpMethod.POST, "/api/user**")
        .permitAll()
        .pathMatchers(HttpMethod.GET, "/api/user**", "/api/user/**")
        .authenticated()
        .pathMatchers(HttpMethod.PUT, "/api/user**", "/api/user/**")
        .authenticated()
        .pathMatchers(HttpMethod.DELETE, "/api/user**")
        .authenticated()
        .pathMatchers(HttpMethod.OPTIONS,"/api/course**", "/api/course/**")
        .permitAll()
        .pathMatchers("/api/course**", "/api/course/**")
        .authenticated()
        .and()
        .csrf()
        .disable()
        .oauth2ResourceServer()
        .jwt();
    return http.build();
  }

  // JWT token store
}
