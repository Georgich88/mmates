package com.georgeisaev.mmates.sherdog.parser.service.impl;

import com.georgeisaev.mmates.common.parser.exception.ParserException;
import com.georgeisaev.mmates.sherdog.domain.Event;
import com.georgeisaev.mmates.sherdog.parser.command.event.EventParserCommand;
import com.georgeisaev.mmates.sherdog.parser.service.EventParserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.georgeisaev.mmates.common.parser.utils.CommonParserUtils.getDocumentFromUrl;
import static com.georgeisaev.mmates.sherdog.parser.utils.Jsoup2SherdogParserUtils.applyParserCommands;
import static com.georgeisaev.mmates.sherdog.parser.utils.SherdogParserUtils.defineIdFromSherdogUrl;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class EventParserServiceImpl implements EventParserService {

  /**
   * Parses a page by url
   *
   * @param url of the site page
   * @return the object parsed by the parser
   * @throws IOException if connecting to Sherdog fails
   * @throws ParserException if anything related to the parser goes wrong
   */
  @Override
  public Event parse(final String url) throws IOException, ParserException {
    log.info("Start. Parse Event from {}", url);

    val commands = EventParserCommand.availableCommands();
    val doc = getDocumentFromUrl(url);
    val builder = applyParserCommands(doc, Event.builder(), commands);
    builder.sherdogUrl(url).id(defineIdFromSherdogUrl(url));
    val event = builder.build();

    log.info("End. Parse Event from {}", url);

    return event;
  }
}
