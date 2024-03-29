package com.georgeisaev.mmates.sherdog.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Event details")
public class Event {

  String id;
  String sherdogUrl;
  String name;
  Promotion promotion;
  LocalDate date;
  List<Fight> fights;
  String location;
}
