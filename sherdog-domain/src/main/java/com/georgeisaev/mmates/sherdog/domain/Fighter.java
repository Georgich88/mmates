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
@Schema(description = "Fighter details")
public class Fighter {

  @Schema(description = "Id")
  String id;

  @Schema(description = "Sherdog url")
  String sherdogUrl;

  @Schema(description = "Sherdog picture url")
  String pictureUrl;

  @Schema(description = "Name")
  String name;

  @Schema(description = "Nickname")
  String nickname;

  @Schema(description = "Birth date")
  LocalDate birthDate;

  @Schema(description = "Address")
  String addressLocality;

  @Schema(description = "Nationality")
  String nationality;

  @Schema(description = "Height, ft")
  String heightFt;

  @Schema(description = "Height, cm")
  String heightCm;

  @Schema(description = "Weight, lbs")
  String weightLbs;

  @Schema(description = "Weight, kg")
  String weightKg;

  @Schema(description = "Association")
  String association;

  @Schema(description = "Weight class")
  String weightClass;

  @Schema(description = "Record")
  FighterRecord fighterRecord;

  @Schema(description = "Fights")
  List<Fight> fights;

  public Fighter postConstruct() {
    if (fights != null) {
      fights.forEach(f -> f.setFirstFighterId(id));
    }
    return this;
  }
}
