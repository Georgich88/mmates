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
public class FighterRecordDetails {

  @Schema(name = "Totals")
  Integer totals;

  @Schema(name = "KO/TKO")
  Integer koTko;

  @Schema(name = "Submission")
  Integer submissions;

  @Schema(name = "Decisions")
  Integer decisions;

  @Schema(name = "Other")
  Integer other;
}
