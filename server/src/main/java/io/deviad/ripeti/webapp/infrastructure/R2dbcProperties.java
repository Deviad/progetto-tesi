package io.deviad.ripeti.webapp.infrastructure;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.r2dbc")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class R2dbcProperties {
  String url;
  String username;
  String password;
  String hostname;
  String name;
  Integer port;
}
