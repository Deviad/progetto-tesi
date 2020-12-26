package io.deviad.ripeti.webapp.infrastructure;

import io.deviad.ripeti.webapp.domain.valueobject.user.FirstName;
import io.deviad.ripeti.webapp.domain.valueobject.user.LastName;
import io.deviad.ripeti.webapp.domain.valueobject.user.Password;
import io.deviad.ripeti.webapp.domain.valueobject.user.Username;
import io.r2dbc.spi.ConnectionFactory;
import lombok.NonNull;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.time.Instant;
import java.util.List;

@Configuration
@EnableR2dbcRepositories(
    basePackages = "io.deviad.ripeti.persistence.repository",
    entityOperationsRef = "myEntityOperations")
public class R2dbcConfiguration extends AbstractR2dbcConfiguration implements InitializingBean {

  @Autowired ConnectionFactory connectionFactory;

  @Override
  protected List<Object> getCustomConverters() {
    return List.of(usernameStringConverter(), passwordStringConverter());
  }

  @Override
  public void afterPropertiesSet() {
    ResourceDatabasePopulator databasePopulator =
        new ResourceDatabasePopulator(
            new ClassPathResource("schema.sql"), new ClassPathResource("data.sql"));

    databasePopulator.populate(connectionFactory).block();
  }

  @Bean
  @Primary
  public R2dbcEntityOperations myEntityOperations() {
    return new R2dbcEntityTemplate(
        client(), new DefaultReactiveDataAccessStrategy(PostgresDialect.INSTANCE));
  }

  @Bean
  DatabaseClient client() {
    return DatabaseClient.create(connectionFactory);
  }

  @Override
  public ConnectionFactory connectionFactory() {
    return connectionFactory;
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

}
