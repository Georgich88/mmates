package com.georgeisaev.mmates.sherdog.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(name = "Fighter stats")
public class FighterRecord {

  @Schema(name = "Wins")
  Integer winsTotals;

  @Schema(name = "Wins (details)")
  FighterRecordDetails winsDetails;

  @Schema(name = "Losses")
  FighterRecordDetails losses;

  @Schema(name = "Losses (details)")
  FighterRecordDetails lossesDetails;

  @Schema(name = "Draws")
  Integer draws;

  @Schema(name = "No contest")
  Integer nc;
}
