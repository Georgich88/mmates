package com.mmates.parsers.sherdog.events;

import com.mmates.core.model.events.Event;
import com.mmates.core.model.fights.Fight;
import com.mmates.core.model.fights.FightResult;
import com.mmates.core.model.fights.WinMethod;
import com.mmates.core.model.people.Fighter;
import com.mmates.core.model.promotion.Promotion;
import com.mmates.parsers.common.Parser;
import com.mmates.parsers.sherdog.utils.SherdogParserUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.mmates.core.model.fights.WinMethod.defineWinMethod;
import static com.mmates.parsers.common.utils.ParserUtils.convertStringToZonedDate;
import static com.mmates.parsers.common.utils.ParserUtils.parseFightResult;
import static com.mmates.parsers.sherdog.events.EventParserConstants.*;
import static com.mmates.parsers.sherdog.utils.SherdogConstants.SELECTOR_TABLE_DATA;
import static java.lang.Integer.parseInt;
import static java.time.ZoneId.systemDefault;
import static java.util.Calendar.SECOND;

/**
 * Parses an event by using its Sherdog URL.
 *
 * @author Georgy Isaev
 * @version 0.1.0
 */
public class EventParser implements Parser<Event> {

    private static final Logger logger = LoggerFactory.getLogger(EventParser.class);
    private final ZoneId timeZone;

    // Constructors

    public EventParser() {
        this(systemDefault());
    }

    public EventParser(ZoneId zoneId) {
        this.timeZone = zoneId;
    }

    // Parsers

    /**
     * Parses an event from a jsoup document
     *
     * @param document the jsoup document
     * @return a parsed event
     */
    @Override
    public Event parseDocument(final Document document) throws ParseException {
        Event event = new Event();
        event.setSherdogUrl(SherdogParserUtils.retrieveSherdogPageUrl(document));
        event.setName(parseEventName(document));
        event.setPromotion(parseDocumentPromotion(document));
        event.setDate(parseEventDate(document));
        event.setFights(retrieveFights(document, event));
        parseEventLocation(document, event);
        return event;
    }

    /**
     * Retrieve a time from a Sherdog's event table cell data.
     *
     * @param timeDataCell element from Sherdog's table
     * @return a fight time
     */
    private int parseTime(final Element timeDataCell) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(MINUTES_SECONDS_DATE_FORMAT.parse(timeDataCell.html()));
            return calendar.get(SECOND);

        } catch (ParseException e) {
            logger.error("", e);
        }
        return 0;
    }

    /**
     * Parses the round at which the even finished
     *
     * @param winRoundDataCell element from Sherdog's table
     * @return the round number
     */
    private int parseWinRound(final Element winRoundDataCell) {
        return parseInt(winRoundDataCell.html());
    }

    /**
     * Parses the win method of the event
     *
     * @param winMethodDataCell element from Sherdog's table
     * @return get the win method
     */
    private WinMethod parseWinMethod(final Element winMethodDataCell) {
        return defineWinMethod(winMethodDataCell.html().replaceAll("<br>(.*)", ""));
    }

    /**
     * Parses the result of the fight
     *
     * @param td element from Sherdog's table
     * @return a fight result enumerator value
     */
    private FightResult parseWinResult(final Element td) {
        return parseFightResult(td);
    }

    private void validateFighterNumber(final int number) {
        if (number < 1 || number > 2) {
            throw new IllegalArgumentException(MSG_ERROR_WRONG_FIGHTER_NUMBER);
        }
    }

    private Fight parseMainFight(Event event, Elements mainFightElement) {
        Fight mainFight = new Fight();
        mainFight.setEvent(event);
        mainFight.setResult(parseFightResult(mainFightElement.first()));
        return mainFight;
    }

    private Fighter parseFighterMainFight(Elements fighters, int number) {
        validateFighterNumber(number);
        Fighter mainFighter = new Fighter();
        Element mainFighterElement = fighters.get(number - 1);
        mainFighter.setSherdogUrl(mainFighterElement.attr("abs:href"));
        mainFighter.setName(mainFighterElement.select("span[itemprop=\"name\"]").html());
        return mainFighter;
    }

    private List<Fight> parseEventFights(final Elements tableRows, final Event event) {
        List<Fight> fights = new ArrayList<>();
        if (!tableRows.isEmpty()) {
            tableRows.remove(0);
            tableRows.forEach(tableRow -> {
                Fight fight = new Fight();
                fight.setEvent(event);
                fight.setDate(event.getDate());
                Elements rowCells = tableRow.select(SELECTOR_TABLE_DATA);
                fight.setFirstFighter(parseFighterFromRowCell(rowCells.get(FIGHTER1_COLUMN)));
                fight.setSecondFighter(parseFighterFromRowCell(rowCells.get(FIGHTER2_COLUMN)));
                // Parse old fight, we can get the result
                if (rowCells.size() == 7) {
                    fight.setResult(parseWinResult(rowCells.get(FIGHTER1_COLUMN)));
                    fight.setWinMethod(parseWinMethod(rowCells.get(METHOD_COLUMN)));
                    fight.setWinRound(parseWinRound(rowCells.get(ROUND_COLUMN)));
                    fight.setWinTime(parseTime(rowCells.get(TIME_COLUMN)));
                }
                fights.add(fight);
            });
        }
        return fights;
    }

    private Fighter parseFighterFromRowCell(Element rowCell) {
        Elements nameElement = rowCell.select(SELECTOR_FIGHTER_NAME);
        if (!nameElement.isEmpty()) {
            String name = nameElement.get(0).html();
            Elements select = rowCell.select(SELECTOR_FIGHTER_DETAILS);
            if (!select.isEmpty()) {
                String url = select.get(0).attr(SELECTOR_FIGHTER_URL);
                Fighter fighter = new Fighter();
                fighter.setSherdogUrl(url);
                fighter.setName(name);
                return fighter;
            }
        }
        return null;
    }

    private ZonedDateTime parseEventDate(Document document) {
        Elements date = document.select(SELECTOR_EVENT_DATE);
        try {
            return convertStringToZonedDate(date.first().attr("content"), timeZone);
        } catch (DateTimeParseException e) {
            logger.error(MSG_ERROR_CANNOT_PARSE_DATE, e);
        }
        return null;
    }

    private Promotion parseDocumentPromotion(Document doc) {
        Elements elements = doc.select(SELECTOR_PROMOTION);
        if (elements.isEmpty()) {
            logger.info(MSG_ERROR_CANNOT_PARSE_PROMOTION);
            return null;
        }
        Element promotionElement = elements.get(0);
        Promotion promotion = new Promotion();
        promotion.setSherdogUrl(promotionElement.attr(SELECTOR_PROMOTION_URL));
        promotion.setName(promotionElement.select(SELECTOR_PROMOTION_NAME).get(0).html());
        return promotion;
    }

    private void parseEventLocation(Document doc, Event event) {
        Elements location = doc.select(SELECTOR_LOCATION);
        event.setLocation(location.html().replace("<br>", " - "));
    }

    private String parseEventName(Document doc) {
        Elements name = doc.select(SELECTOR_EVENT_NAME);
        return name.html().replace("<br>", " - ");
    }

    private List<Fight> retrieveFights(final Document document, final Event event) throws ParseException {
        List<Fight> fights = new ArrayList<>();
        final Elements mainFightElement = document.select(SELECTOR_MAIN_FIGHT);
        final Elements fighters = mainFightElement.select(SELECTOR_MAIN_FIGHT_FIGHTERS);
        // Check if events has details about fighters.
        // For canceled events there is no info about main events and fighters.
        if (fighters.size() > 1) {
            Fighter firstFighter = parseFighterMainFight(fighters, 1);
            Fighter secondFighter = parseFighterMainFight(fighters, 2);
            Fight mainFight = parseMainFight(event, mainFightElement);
            mainFight.setFirstFighter(firstFighter);
            mainFight.setSecondFighter(secondFighter);
            parseResult(mainFightElement, mainFight);
            mainFight.setDate(event.getDate());
            fights.add(mainFight);
        }
        Elements tds = document.select(SELECTOR_EVENT_FIGHTS);
        fights.addAll(parseEventFights(tds, event));
        return fights;
    }

    private void parseResult(Elements mainFightElement, Fight mainFight) throws ParseException {
        final Elements mainFightDataCell = mainFightElement.select(SELECTOR_TABLE_DATA);
        if (!mainFightDataCell.isEmpty()) {
            var winMethod = defineWinMethod(mainFightDataCell.get(1).html().replaceAll("<em(.*)<br>", "").trim());
            int winRound = parseInt(mainFightDataCell.get(3).html().replaceAll("<em(.*)<br>", "").trim());
            String minutesSecondsWinTime = mainFightDataCell.get(4).html().replaceAll("<em(.*)<br>", "").trim();
            Date date = MINUTES_SECONDS_DATE_FORMAT.parse(minutesSecondsWinTime);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int winTime = calendar.get(SECOND);
            mainFight.setWinMethod(winMethod);
            mainFight.setWinRound(winRound);
            mainFight.setWinTime(winTime);
        }
    }

}
