package io.deviad.ripeti.webapp.adapter;

import com.fasterxml.jackson.annotation.JsonValue;

public interface DataSizeMixin {
  @JsonValue
  long toBytes();
}
