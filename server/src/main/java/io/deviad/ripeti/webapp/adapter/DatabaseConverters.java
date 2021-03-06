package io.deviad.ripeti.webapp.adapter;

import io.deviad.ripeti.webapp.domain.valueobject.course.CourseStatus;
import io.deviad.ripeti.webapp.domain.valueobject.user.Address;
import io.deviad.ripeti.webapp.domain.valueobject.user.Role;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class DatabaseConverters {

  public List<Object> getConvertersToRegister() {
    List<Object> list = new ArrayList<>();
    list.add(instantToTimestamp());
    list.add(roleConverter());
    list.add(statusConverter());
    list.add(addressToDbConverter());
    list.add(addressFromDbConverter());
    return list;
  }

  @Bean
  public InstantToTimestamp instantToTimestamp() {
    return new InstantToTimestamp();
  }

  @Bean
  public RoleConverter roleConverter() {
    return new RoleConverter();
  }

  @Bean
  public StatusConverter statusConverter() {
    return new StatusConverter();
  }

  @Bean
  public AddressToDbConverter addressToDbConverter() {
    return new AddressToDbConverter();
  }

  @Bean
  public AddressFromDbConverter addressFromDbConverter() {
    return new AddressFromDbConverter();
  }

  @WritingConverter
  public class InstantToTimestamp implements Converter<Instant, Timestamp> {

    @Override
    public Timestamp convert(@NonNull Instant time) {
      return Timestamp.from(time);
    }
  }

  @WritingConverter
  @ReadingConverter
  public class RoleConverter implements Converter<Role, Role> {

    @Override
    public Role convert(@NonNull Role role) {
      return role;
    }
  }

  @WritingConverter
  @ReadingConverter
  public class StatusConverter implements Converter<CourseStatus, CourseStatus> {

    @Override
    public CourseStatus convert(@NonNull CourseStatus status) {
      return status;
    }
  }

  @WritingConverter
  public class AddressToDbConverter implements Converter<Address, String> {
    @Override
    public String convert(Address address) {
      return MappingUtils.toJson(address);
    }
  }

  @ReadingConverter
  public class AddressFromDbConverter implements Converter<String, Address> {
    @Override
    public Address convert(String address) {
      return MappingUtils.fromJson(address, Address.class);
    }
  }
}
