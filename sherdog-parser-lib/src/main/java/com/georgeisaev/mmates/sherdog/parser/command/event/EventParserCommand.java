package com.georgeisaev.mmates.sherdog.parser.command.event;

import com.georgeisaev.mmates.sherdog.domain.Event;
import com.georgeisaev.mmates.sherdog.parser.command.Jsoup2SherdogParserCommand;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;


@Slf4j
@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum EventParserCommand implements Jsoup2SherdogParserCommand<Event.EventBuilder> {

  // name
  NAME("name", ".event_detail h1 span[itemprop=\"name\"]"),

  // location
  LOCATION("location", ".event_detail span[itemprop=\"location\"]");

  /** Attribute name */
  String attribute;

  /** CSS-like element selector, that finds elements matching a query */
  String selector;

  public static Collection<Jsoup2SherdogParserCommand<Event.EventBuilder>> availableCommands() {
    return List.of(values());
  }
}
