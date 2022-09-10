package com.georgeisaev.mmates.sherdog.parser.data.parser.fighter;

import com.georgeisaev.mmates.common.parser.command.JsopAttributeParserCommand;
import com.georgeisaev.mmates.common.parser.exception.ParserException;
import com.georgeisaev.mmates.sherdog.domain.Event;
import com.georgeisaev.mmates.sherdog.domain.Fight;
import com.georgeisaev.mmates.sherdog.domain.FightResult;
import com.georgeisaev.mmates.sherdog.domain.FightType;
import com.georgeisaev.mmates.sherdog.domain.Fighter;
import com.georgeisaev.mmates.sherdog.domain.WinMethod;
import com.georgeisaev.mmates.sherdog.parser.utils.SherdogParserUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import static com.georgeisaev.mmates.common.parser.utils.DateTimeUtils.parseMinutesSeconds;
import static com.georgeisaev.mmates.sherdog.parser.data.parser.fighter.FighterFightsAttributeParserCommand.FightTableColumns.COLUMN_EVENT;
import static com.georgeisaev.mmates.sherdog.parser.data.parser.fighter.FighterFightsAttributeParserCommand.FightTableColumns.COLUMN_METHOD;
import static com.georgeisaev.mmates.sherdog.parser.data.parser.fighter.FighterFightsAttributeParserCommand.FightTableColumns.COLUMN_OPPONENT;
import static com.georgeisaev.mmates.sherdog.parser.data.parser.fighter.FighterFightsAttributeParserCommand.FightTableColumns.COLUMN_RESULT;
import static com.georgeisaev.mmates.sherdog.parser.data.parser.fighter.FighterFightsAttributeParserCommand.FightTableColumns.COLUMN_ROUND;
import static com.georgeisaev.mmates.sherdog.parser.data.parser.fighter.FighterFightsAttributeParserCommand.FightTableColumns.COLUMN_TIME;
import static com.georgeisaev.mmates.sherdog.parser.utils.SherdogParserUtils.defineIdFromSherdogUrl;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsFirst;

@Slf4j
@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum FighterFightsAttributeParserCommand
    implements JsopAttributeParserCommand<Fighter.FighterBuilder> {

  // fights history - pro
  FIGHTS("fights", ".tiled_bg.latest_features .slanted_title") {
    @Override
    public void parse(Document doc, Fighter.FighterBuilder builder) {

      final Elements tableTitles = doc.select(getSelector());
      final Collection<Element> foundTitles =
          tableTitles.stream()
              .filter(element -> element.text().startsWith(FIGHT_HISTORY_TABLE_TITLE_PREFIX))
              .toList();

      final List<Fight> fights = new ArrayList<>();

      foundTitles.stream()
          .filter(title -> title.parent() != null && title.parent().parent() != null)
          .forEach(
              title -> {
                final FightType fightType =
                    FightType.fromString(
                        title.text().replace(FIGHT_HISTORY_TABLE_TITLE_PREFIX, ""));
                title
                    .parent()
                    .parent()
                    .select(".module.fight_history .new_table_holder table tr")
                    .stream()
                    .skip(1) // skip table head
                    .forEach(
                        tableRow -> {
                          final Elements dataCells = tableRow.select("td");
                          final Fighter opponent = parseOpponent(dataCells.get(COLUMN_OPPONENT));
                          final String methodAndDetails =
                              parseWinMethod(dataCells.get(COLUMN_METHOD));
                          final Fight fight =
                              Fight.builder()
                                  .result(parseFightResult(dataCells.get(COLUMN_RESULT)))
                                  .secondFighterId(opponent.getId())
                                  .eventId(parseEvent(dataCells.get(COLUMN_EVENT)).getId())
                                  .date(parseDate(dataCells.get(COLUMN_EVENT)))
                                  .winMethod(WinMethod.winMethod(methodAndDetails))
                                  .winMethodDetails(WinMethod.winMethodDetails(methodAndDetails))
                                  .winRound(parseWinRound(dataCells.get(COLUMN_ROUND)))
                                  .winTime(parseWinTime(dataCells.get(COLUMN_TIME)))
                                  .type(fightType)
                                  .build();
                          fights.add(fight);
                        });
              });

      builder.fights(
          fights.stream().sorted(comparing(Fight::getDate, nullsFirst(naturalOrder()))).toList());
    }
  };

  public static final String FIGHT_HISTORY_TABLE_TITLE_PREFIX = "FIGHT HISTORY - ";
  private static final DateTimeFormatter DATE_TIME_FORMATTER_MMM_DD_YYYY =
      DateTimeFormatter.ofPattern("MMM / dd / " + "yyyy", Locale.US);

  /** Attribute name */
  String attribute;

  /** CSS-like element selector, that finds elements matching a query */
  String selector;

  public static Collection<JsopAttributeParserCommand<Fighter.FighterBuilder>> availableCommands() {
    return List.of(values());
  }

  /**
   * Get the fight result
   *
   * @param td a td from sherdog table
   * @return a fight result enum
   */
  private static FightResult parseFightResult(Element td) {
    return SherdogParserUtils.parseFightResult(td);
  }

  /**
   * Get the fight result
   *
   * @param td a td from sherdog table
   * @return a fight result enum
   */
  private static Fighter parseOpponent(Element td) {
    final Element opponentLink = td.select("a").get(0);
    final String sherdogUrl = opponentLink.attr("abs:href");
    return Fighter.builder()
        .name(opponentLink.html())
        .sherdogUrl(sherdogUrl)
        .id(defineIdFromSherdogUrl(sherdogUrl))
        .build();
  }

  /**
   * Get the fight event
   *
   * @param td a td from sherdog table
   * @return a sherdog base object with url and name
   */
  private static Event parseEvent(Element td) {
    Element link = td.select("a").get(0);
    var eventBuilder = Event.builder();
    eventBuilder.name(link.html().replaceAll("<span itemprop=\"award\">|</span>", ""));
    final String sherdogUrl = link.attr("abs:href");
    eventBuilder.sherdogUrl(sherdogUrl);
    eventBuilder.id(defineIdFromSherdogUrl(sherdogUrl));
    return eventBuilder.build();
  }

  /**
   * Get the date of the fight
   *
   * @param td a td from sherdog table
   * @return the local datetime of the fight
   */
  private static LocalDate parseDate(Element td) {
    // date
    Element dateElement = td.select("span.sub_line").first();
    try {
      assert dateElement != null;
      return LocalDate.parse(dateElement.text(), DATE_TIME_FORMATTER_MMM_DD_YYYY);
    } catch (Exception e) {
      log.debug("", e);
    }
    try {
      TextNode dateText = (TextNode) dateElement.childNode(0);
      return LocalDate.parse(dateText.text(), DATE_TIME_FORMATTER_MMM_DD_YYYY);
    } catch (Exception e) {
      log.debug("", e);
    }
    return null;
  }

  /**
   * Get the winning method
   *
   * @param td a td from sherdog table
   * @return a string with the finishing method
   */
  private static String parseWinMethod(final Element td) {
    return td.children().get(0).text();
  }

  /**
   * Get the winning round
   *
   * @param td a td from sherdog table
   * @return an integer
   */
  private static int parseWinRound(Element td) {
    return Integer.parseInt(td.html());
  }

  /**
   * Get time of win
   *
   * @param td a td from sherdog table
   * @return the time of win
   */
  private static int parseWinTime(Element td) {
    try {
      return parseMinutesSeconds(td.html());
    } catch (ParserException e) {
      return 0;
    }
  }

  @UtilityClass
  static final class FightTableColumns {

    static final int COLUMN_RESULT = 0;
    static final int COLUMN_OPPONENT = 1;
    static final int COLUMN_EVENT = 2;
    static final int COLUMN_METHOD = 3;
    static final int COLUMN_ROUND = 4;
    static final int COLUMN_TIME = 5;
  }
}
