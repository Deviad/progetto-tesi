//package io.deviad.ripeti.webapp.persistence;
//
//import com.fasterxml.jackson.annotation.JsonAutoDetect;
//import com.fasterxml.jackson.annotation.JsonInclude;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Value;
//import lombok.With;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.relational.core.mapping.Column;
//import org.springframework.data.relational.core.mapping.Table;
//
//import java.util.UUID;
//
//@Table("addresses")
//@Value
//@With
//@Builder
//@AllArgsConstructor
//@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
//@JsonInclude(JsonInclude.Include.NON_DEFAULT)
//public class AddressEntity {
//  @Id
//  UUID id;
//
//  @Column("first_address_line")
//  String firstAddressLine;
//
//  @Column("second_address_line")
//  String secondAddressLine;
//
//  @Column("city")
//  String city;
//
//  @Column("country")
//  String country;
//}
