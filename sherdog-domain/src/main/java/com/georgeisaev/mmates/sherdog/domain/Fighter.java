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
@Schema(name = "Fighter details")
public class Fighter {

  @Schema(name = "Id")
  String id;

  @Schema(name = "Sherdog url")
  String sherdogUrl;

  @Schema(name = "Sherdog picture url")
  String pictureUrl;

  @Schema(name = "Name")
  String name;

  @Schema(name = "Nickname")
  String nickname;

  @Schema(name = "Birth date")
  LocalDate birthDate;

  @Schema(name = "Address")
  String addressLocality;

  @Schema(name = "Nationality")
  String nationality;

  @Schema(name = "Height, ft")
  String heightFt;

  @Schema(name = "Height, cm")
  String heightCm;

  @Schema(name = "Weight, lbs")
  String weightLbs;

  @Schema(name = "Weight, kg")
  String weightKg;

  @Schema(name = "Association")
  String association;

  @Schema(name = "Weight class")
  String weightClass;

  @Schema(name = "Record")
  FighterRecord record;

  @Schema(name = "Fights")
  List<Fight> fights;

  public Fighter postConstruct() {
    if (fights != null) {
      fights.forEach(f -> f.setFirstFighterId(id));
    }
    return this;
  }

}
