package com.georgeisaev.mmates.sherdog.parser.utils;

import com.georgeisaev.mmates.sherdog.domain.FightResult;
import com.georgeisaev.mmates.sherdog.parser.exception.IllegalSherdogUrlException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;

@Slf4j
@UtilityClass
public class SherdogParserUtils {

  public static final String MSG_ERR_CANNOT_PARSE_PROPERTY = "Cannot parse property {} from {}";

  /**
   * Defines id from Sherdog url
   *
   * @param sherdogUrl sherdog item url
   * @return id from url
   */
  public static String defineIdFromSherdogUrl(String sherdogUrl) {
    String[] parts = sherdogUrl.split("/");
    if (parts.length > 0) {
      return parts[parts.length - 1];
    } else {
      throw new IllegalSherdogUrlException("Cannot define fighter ID from url");
    }
  }

  /**
   * Gets the result of a fight following sherdog website win/lose/draw/nc Make sure to use on
   * Fighter1 only
   *
   * @param element Jsoup element
   * @return a FightResult
   */
  public static FightResult parseFightResult(Element element) {
    if (!element.select(".win").isEmpty()) {
      return FightResult.FIGHTER_1_WIN;
    } else if (!element.select(".loss").isEmpty()) {
      return FightResult.FIGHTER_2_WIN;
    } else if (!element.select(".draw").isEmpty()) {
      return FightResult.DRAW;
    } else if (!element.select(".no_contest").isEmpty()) {
      return FightResult.NO_CONTEST;
    } else {
      return FightResult.NOT_HAPPENED;
    }
  }
}
