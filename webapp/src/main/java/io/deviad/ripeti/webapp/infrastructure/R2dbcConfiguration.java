package io.deviad.ripeti.webapp.infrastructure;

import io.deviad.ripeti.webapp.domain.valueobject.course.Status;
import io.deviad.ripeti.webapp.domain.valueobject.user.Address;
import io.deviad.ripeti.webapp.domain.valueobject.user.FirstName;
import io.deviad.ripeti.webapp.domain.valueobject.user.LastName;
import io.deviad.ripeti.webapp.domain.valueobject.user.Password;
import io.deviad.ripeti.webapp.domain.valueobject.user.Role;
import io.deviad.ripeti.webapp.domain.valueobject.user.Username;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.postgresql.codec.EnumCodec;
import io.r2dbc.postgresql.extension.CodecRegistrar;
import io.r2dbc.spi.ConnectionFactory;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.core.DefaultReactiveDataAccessStrategy;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.PostgresDialect;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import org.springframework.r2dbc.core.DatabaseClient;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.List;

@Configuration
@EnableR2dbcRepositories(
    basePackages = "io.deviad.ripeti.persistence.repository",
    entityOperationsRef = "myEntityOperations")
public class R2dbcConfiguration extends AbstractR2dbcConfiguration
    implements InitializingBean, ApplicationContextAware {

  @Override
  protected List<Object> getCustomConverters() {
    return List.of(
        usernameStringConverter(),
        passwordStringConverter(),
        roleConverter(),
        statusConverter(),
        addressConverter());
  }

  @Override
  public void afterPropertiesSet() {
    ResourceDatabasePopulator databasePopulator =
        new ResourceDatabasePopulator(
            new ClassPathResource("schema.sql"),
            new ClassPathResource("functions.sql"),
            new ClassPathResource("data.sql"));

    databasePopulator.populate(connectionFactory()).block();
  }

  @Bean
  @Primary
  public R2dbcEntityOperations myEntityOperations() {
    return new R2dbcEntityTemplate(
        client(), new DefaultReactiveDataAccessStrategy(PostgresDialect.INSTANCE));
  }

  @Bean
  DatabaseClient client() {
    return DatabaseClient.create(connectionFactory());
  }

  @SneakyThrows
  @Bean
  @Primary
  public ConnectionFactory connectionFactory() {
    CodecRegistrar roleEnumCodec = EnumCodec.builder().withEnum("user_role", Role.class).build();
    CodecRegistrar statusEnumCodec =
        EnumCodec.builder().withEnum("course_status", Status.class).build();
    return new PostgresqlConnectionFactory(
        PostgresqlConnectionConfiguration.builder()
            .host(r2dbcProperties().getHostname())
            .database(r2dbcProperties().getName())
            .port(r2dbcProperties().getPort())
            .codecRegistrar(roleEnumCodec)
            .codecRegistrar(statusEnumCodec)
            .username(r2dbcProperties().getUsername())
            .password(r2dbcProperties().getPassword())
            .build());
  }

  @Bean
  public Converter<Username, String> usernameStringConverter() {
    return new Converter<Username, String>() {
      @Override
      public String convert(@NonNull Username username) {
        return username.username();
      }
    };
  }

  @Bean
  public Converter<Password, String> passwordStringConverter() {
    return new Converter<Password, String>() {
      @Override
      public String convert(@NonNull Password password) {
        return password.password();
      }
    };
  }

  @Bean
  public Converter<FirstName, String> firstNameStringConverter() {
    return new Converter<FirstName, String>() {
      @Override
      public String convert(@NonNull FirstName firstName) {
        return firstName.firstName();
      }
    };
  }

  @Bean
  public Converter<LastName, String> lastNameStringConverter() {
    return new Converter<LastName, String>() {
      @Override
      public String convert(@NonNull LastName lastName) {
        return lastName.lastName();
      }
    };
  }

  @Bean
  public Converter<Instant, Timestamp> instantTimestampConverter() {
    return new Converter<Instant, Timestamp>() {
      @Override
      public Timestamp convert(@NonNull Instant time) {
        return Timestamp.from(time);
      }
    };
  }

  /*
  This is required because by default Spring Data converts enums into strings.
   */

  @Bean
  public Converter<Role, Role> roleConverter() {
    return new Converter<Role, Role>() {
      @Override
      public Role convert(@NonNull Role role) {
        return role;
      }
    };
  }

  @Bean
  public Converter<Status, Status> statusConverter() {
    return new Converter<Status, Status>() {
      @Override
      public Status convert(@NonNull Status status) {
        return status;
      }
    };
  }

  @Bean
  public Converter<Address, String> addressConverter() {
    var separator = "\\|";
    return new Converter<Address, String>() {

      @Override
      public String convert(@NonNull Address address) {

        var params =
            new String[] {
              separator,
              address.firstAddressLine(),
              address.secondAddressLine(),
              address.city(),
              address.country()
            };

        return MessageFormat.format("{1} {0} {2} {0} {3} {0} {4}", params);
      }
    };
  }

  @Bean
  public R2dbcProperties r2dbcProperties() {
    return new R2dbcProperties();
  }
}
