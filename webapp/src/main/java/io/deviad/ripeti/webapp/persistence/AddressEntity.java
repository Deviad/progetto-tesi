package io.deviad.ripeti.webapp.persistence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressEntity {
  @Id Long id;

  @Column("first_address_line")
  String firstAddressLine;

  @Column("second_address_line")
  String secondAddressLine;

  @Column("city")
  String city;

  @Column("country")
  String country;
}
