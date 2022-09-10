package com.georgeisaev.mmates.sherdog.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Fight details")
public class Fight {

  String eventId;
  String firstFighterId;
  String secondFighterId;
  LocalDate date;
  FightResult result;
  WinMethod winMethod;
  String winMethodDetails;
  Integer winTime;
  Integer winRound;
  FightType type;
}
