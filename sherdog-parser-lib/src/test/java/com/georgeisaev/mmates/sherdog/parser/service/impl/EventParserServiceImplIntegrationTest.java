package com.georgeisaev.mmates.sherdog.parser.service.impl;

import com.georgeisaev.mmates.sherdog.domain.Event;
import com.georgeisaev.mmates.sherdog.parser.MmatesSherdogParserApplication;
import com.georgeisaev.mmates.sherdog.parser.service.EventParserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = {MmatesSherdogParserApplication.class, EventParserServiceImpl.class})
class EventParserServiceImplIntegrationTest {

  private static final String BELLATOR_208_URL =
      "https://www.sherdog.com/events/Bellator-208-Fedor-vs-Sonnen-69635";

  @Autowired EventParserService eventParserService;

  @Test
  @DisplayName("Should parse an event")
  void shouldParseValidEventUrl() {

    // GIVEN
    assertNotNull(eventParserService);

    // WHEN
    final Event event = assertDoesNotThrow(() -> eventParserService.parse(BELLATOR_208_URL));

    // THEN
    assertNotNull(event);
    assertThat(event.getName()).isNotEmpty();
    assertThat(event.getSherdogUrl()).isNotEmpty();
    assertThat(event.getLocation()).isNotEmpty();
  }
}
