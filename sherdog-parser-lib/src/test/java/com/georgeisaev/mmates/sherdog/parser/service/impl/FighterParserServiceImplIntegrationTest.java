package com.georgeisaev.mmates.sherdog.parser.service.impl;

import com.georgeisaev.mmates.sherdog.domain.Fighter;
import com.georgeisaev.mmates.sherdog.parser.service.FighterParserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataMongoTest
@Import({FighterParserServiceImpl.class})
class FighterParserServiceImplIntegrationTest {

  private static final String KHAMZAT_CHIMAEV_URL =
      "https://www.sherdog.com/fighter/Khamzat-Chimaev-280021";
  private static final String TYRON_WOODLEY_URL =
      "https://www.sherdog.com/fighter/Tyron-Woodley-42605";

  private static final String FEDOR_EMELIANENKO_URL =
      "https://www.sherdog.com/fighter/Fedor-Emelianenko-1500";
  @Autowired FighterParserService fighterParserService;

  @Test
  @DisplayName("Should parse a fighter with wins")
  void shouldParseValidFighterUrl() {

    // GIVEN
    assertNotNull(fighterParserService);

    // WHEN
    final Fighter khamzatChimaev =
        assertDoesNotThrow(() -> fighterParserService.parse(KHAMZAT_CHIMAEV_URL));

    // THEN
    assertNotNull(khamzatChimaev);
    assertNotNull(khamzatChimaev.getFighterRecord());
    assertThat(khamzatChimaev.getFighterRecord().getWinsTotals()).isPositive();
  }

  @Test
  @DisplayName("Should parse a fighter with draws")
  void shouldParseValidFighterWithDraws() {

    // GIVEN
    assertNotNull(fighterParserService);

    // WHEN
    final Fighter tyronWoodley =
        assertDoesNotThrow(() -> fighterParserService.parse(TYRON_WOODLEY_URL));

    // THEN
    assertNotNull(tyronWoodley);
    assertNotNull(tyronWoodley.getFighterRecord());
    assertThat(tyronWoodley.getFighterRecord().getDraws()).isPositive();
  }

  @Test
  @DisplayName("Should parse a fighter with N/C")
  void shouldParseValidFighterWithNc() {

    // GIVEN
    assertNotNull(fighterParserService);

    // WHEN
    final Fighter fedorEmelianenko =
        assertDoesNotThrow(() -> fighterParserService.parse(FEDOR_EMELIANENKO_URL));

    // THEN
    assertNotNull(fedorEmelianenko);
    assertNotNull(fedorEmelianenko.getFighterRecord());
    assertThat(fedorEmelianenko.getFighterRecord().getNc()).isPositive();
  }
}
