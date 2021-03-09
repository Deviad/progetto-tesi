package io.deviad.ripeti.webapp.infrastructure;

import io.deviad.ripeti.webapp.adapter.DatabaseConverters;
import io.deviad.ripeti.webapp.domain.valueobject.course.CourseStatus;
import io.deviad.ripeti.webapp.domain.valueobject.user.Role;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.postgresql.codec.EnumCodec;
import io.r2dbc.postgresql.extension.CodecRegistrar;
import io.r2dbc.spi.ConnectionFactory;
import lombok.SneakyThrows;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.core.DefaultReactiveDataAccessStrategy;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.dialect.PostgresDialect;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import org.springframework.r2dbc.core.DatabaseClient;

import java.util.List;

@Configuration
@EnableR2dbcRepositories(
    basePackages = "io.deviad.ripeti.persistence.repository",
    entityOperationsRef = "myEntityOperations")
public class R2dbcConfiguration extends AbstractR2dbcConfiguration
    implements InitializingBean, ApplicationContextAware {

  @Autowired DatabaseConverters converters;

  @Override
  protected List<Object> getCustomConverters() {
    return converters.getConvertersToRegister();
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
        EnumCodec.builder().withEnum("course_status", CourseStatus.class).build();
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
  public R2dbcProperties r2dbcProperties() {
    return new R2dbcProperties();
  }
}
