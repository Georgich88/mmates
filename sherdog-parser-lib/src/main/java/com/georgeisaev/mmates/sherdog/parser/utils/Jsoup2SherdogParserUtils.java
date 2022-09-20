package com.georgeisaev.mmates.sherdog.parser.utils;

import com.georgeisaev.mmates.sherdog.parser.command.Jsoup2SherdogParserCommand;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;

import java.util.Collection;

import static com.georgeisaev.mmates.sherdog.parser.utils.SherdogParserUtils.MSG_ERR_CANNOT_PARSE_PROPERTY;

@Slf4j
@UtilityClass
public class Jsoup2SherdogParserUtils {

  public static <T, C extends Jsoup2SherdogParserCommand<T>> T applyParserCommands(
      Document source, T target, Collection<C> commands) {
    commands.forEach(
        c -> {
          try {
            c.parse(source, target);
          } catch (Exception e) {
            log.error(MSG_ERR_CANNOT_PARSE_PROPERTY, c.getAttribute(), target, e);
          }
        });
    return target;
  }
}
