package com.mmates.parsers.sherdog.events;

import com.mmates.core.model.events.Event;
import com.mmates.core.model.fights.Fight;
import com.mmates.core.model.fights.FightResult;
import com.mmates.core.model.fights.WinMethod;
import com.mmates.core.model.people.Fighter;
import com.mmates.core.model.promotion.Promotion;
import com.mmates.parsers.common.Parser;
import com.mmates.parsers.common.exceptions.ParserException;
import com.mmates.parsers.common.utils.Constants;
import com.mmates.parsers.common.utils.ParserUtils;
import com.mmates.parsers.sherdog.Sherdog;
import com.mmates.parsers.sherdog.utils.SherdogParserUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Class parses an event by using its Sherdog URL
 */
public class EventParser implements Parser<Event> {

    private static final int FIGHTER1_COLUMN = 1;
    private static final int FIGHTER2_COLUMN = 3;
    private static final int METHOD_COLUMN = 4;
    private static final int ROUND_COLUMN = 5;
    private static final int TIME_COLUMN = 6;

    // Selectors
    public static final String SELECTOR_TABLE_DATA = "td";
    public static final String SELECTOR_RECENT_EVENTS_TABLE = "#recentfights_tab .event tr";
    public static final String MESSAGE_INFO_GETTING_RECENT_EVENTS = "Getting recent events {} ";
    public static final String MESSAGE_ERROR_CANNOT_PARSE_DATE = "Couldn't parse date";
    public static final String SELECTOR_PROMOTION_NAME = "span[itemprop=\"name\"]";
    public static final String SELECTOR_PROMOTION_URL = "abs:href";
    public static final String SELECTOR_PROMOTION = ".header .section_title h2 a";
    public static final String MESSAGE_ERROR_PROMOTION_IS_NOT_IN_SHERDOG_DATABASE = "Promotion for the event {} is not in Sherdog database";
    public static final String SELECTOR_LOCATION = "span[itemprop=\"location\"]";
    public static final String SELECTOR_EVENT_NAME = ".header .section_title h1 span[itemprop=\"name\"]";
    public static final String SELECTOR_MAIN_FIGHT = ".content.event";
    public static final String SELECTOR_MAIN_FIGHT_FIGHTERS = "h3 a";
    public static final String SELECTOR_EVENT_FIGHTS = ".event_match table tr";
    public static final String SELECTOR_EVENT_DATE = ".authors_info .date meta[itemprop=\"startDate\"]";
    public static final String SELECTOR_FIGHTER_URL = "abs:href";
    public static final String SELECTOR_FIGHTER_NAME = "span[itemprop=\"name\"]";
    public static final String SELECTOR_FIGHTER_DETAILS = "a[itemprop=\"url\"]";

    private final ZoneId ZONE_ID;

    public static final String SHERDOG_RECENT_EVENT_URL_TEMPLATE = "https://www.sherdog.com/events/recent/%d-page/";
    public static final SimpleDateFormat MINUTES_SECONDS_DATE_FORMAT = new SimpleDateFormat("mm:ss");

    private final Logger logger = LoggerFactory.getLogger(EventParser.class);

    private boolean fastMode = false;
    public boolean isFastMode() {
        return fastMode;
    }
    public void setFastMode(boolean fastMode) {
        this.fastMode = fastMode;
    }

    // Constructors

    /**
     * Creates an event parser with the default zone id
     */
    public EventParser() {
        this.ZONE_ID = ZoneId.systemDefault();
    }

    /**
     * Setting a zoneId will convert the dates to the desired zone id
     *
     * @param zoneId specified zone id for time conversion
     */
    public EventParser(ZoneId zoneId) {
        this.ZONE_ID = zoneId;
    }

    // Parsers

    /**
     * Parses recent events from the event page
     *
     * @param start the start position
     * @param depth amount of events to parse
     * @return recent events
     * @throws IOException
     * @throws ParseException
     * @throws ParserException
     */
    public List<Event> parseRecentEvents(int start, int depth) throws IOException, ParseException, ParserException {

        List<Event> events = new ArrayList<>();

        String url = SHERDOG_RECENT_EVENT_URL_TEMPLATE;
        int currentPageNumber = start;
        Sherdog sherdog = new Sherdog.Builder().withTimezone(Constants.SHERDOG_TIME_ZONE).build();
        sherdog.setFastMode(isFastMode());

        logger.info(MESSAGE_INFO_GETTING_RECENT_EVENTS, String.format(url, currentPageNumber));

        do {

            Document doc = ParserUtils.parseDocument(String.format(url, currentPageNumber));
            Elements eventElements = doc.select(SELECTOR_RECENT_EVENTS_TABLE);

            if (eventElements.size() > 0) {
                eventElements.remove(0);
                eventElements.forEach(tr -> {

                    Elements tds = tr.select(SELECTOR_TABLE_DATA);
                    String eventUrl = getEventUrl(tds.get(1));
                    Event event;

                    try {
                        event = sherdog.getEvent(eventUrl);
                        if (event != null) {
                            events.add(event);
                        }
                    } catch (Throwable error) {
                        logger.error(error.getMessage());
                        error.printStackTrace();
                    }

                });
            }

            currentPageNumber++;

        } while (currentPageNumber < start + depth);

        return events;
    }

    // TODO: Refactoring, duplicated method with PromotionParser.
    private String getEventUrl(Element td) {
        Elements url = td.select("a[itemprop=\"url\"]");
        if (url.size() > 0) {
            String attr = url.get(0).attr("abs:href");
            return attr;
        } else {
            return "";
        }
    }

    /**
     * Parses an event from a jsoup document
     *
     * @param doc the jsoup document
     * @return a parsed event
     */
    @Override
    public Event parseDocument(Document doc) throws ParseException {

        Event event = new Event();

        event.setSherdogUrl(SherdogParserUtils.getSherdogPageUrl(doc));

        parseEventName(doc, event);
        parseDocumentPromotion(doc, event);
        parseEventDate(doc, event);
        retrieveFights(doc, event);
        parseEventLocation(doc, event);

        return event;
    }

    private void parseEventDate(Document doc, Event event) {

        if (isFastMode())
            return;

        Elements date = doc.select(SELECTOR_EVENT_DATE);
        // TODO: get date to proper format
        try {
            event.setDate(ParserUtils.getDateFromStringToZoneId(date.first().attr("content"), ZONE_ID));
        } catch (DateTimeParseException error) {
            logger.error(MESSAGE_ERROR_CANNOT_PARSE_DATE, error);
        }
    }

    private void parseDocumentPromotion(Document doc, Event event) {

        Elements elements = doc.select(SELECTOR_PROMOTION);
        if (elements.size() == 0) {
            logger.info(MESSAGE_ERROR_PROMOTION_IS_NOT_IN_SHERDOG_DATABASE, event.getName());
            return;
        }
        Element promotionElement = elements.get(0);
        Promotion promotion = new Promotion();
        promotion.setSherdogUrl(promotionElement.attr(SELECTOR_PROMOTION_URL));
        promotion.setName(promotionElement.select(SELECTOR_PROMOTION_NAME).get(0).html());

        event.setPromotion(promotion);
    }

    private void parseEventLocation(Document doc, Event event) {

        if (isFastMode())
            return;

        Elements location = doc.select(SELECTOR_LOCATION);
        event.setLocation(location.html().replace("<br>", " - "));
    }

    private void parseEventName(Document doc, Event event) {

        Elements name = doc.select(SELECTOR_EVENT_NAME);
        event.setName(name.html().replace("<br>", " - "));

    }

    /**
     * Gets the fights of the event
     *
     * @param doc   the jsoup HTML document
     * @param event The current event
     * @throws ParserException
     * @throws ParseException
     * @throws IOException
     */
    private void retrieveFights(Document doc, Event event) throws ParseException {

        if (isFastMode())
            return;

        // logger.info("Getting fights for event #{}[{}]", event.getSherdogUrl(),
        // event.getName());
        List<Fight> fights = new ArrayList<>();

        // Checking on main event
        Elements mainFightElement = doc.select(SELECTOR_MAIN_FIGHT);

        Elements fighters = mainFightElement.select(SELECTOR_MAIN_FIGHT_FIGHTERS);

        // Check if events has details about fighters.
        // For canceled events there is no info about main events and fighters.
        if (fighters.size() > 1) {

            Fighter firstFighter = getFighterMainFight(fighters, 1);
            Fighter secondFighter = getFighterMainFight(fighters, 2);
            Fight mainFight = getMainFight(event, mainFightElement);
            mainFight.setFighter1(firstFighter);
            mainFight.setFighter2(secondFighter);
            defineResultMethod(mainFightElement, mainFight);
            mainFight.setDate(event.getDate());

            fights.add(mainFight);
        }

        Elements tds = doc.select(SELECTOR_EVENT_FIGHTS);

        fights.addAll(parseEventFights(tds, event));

        event.setFights(fights);
    }

    private void defineResultMethod(Elements mainFightElement, Fight mainFight) throws ParseException {

        Elements mainTd = mainFightElement.select(SELECTOR_TABLE_DATA);

        if (mainTd.size() > 0) {

            var winMethod = WinMethod.defineWinMethod(mainTd.get(1).html().replaceAll("<em(.*)<br>", "").trim());
            int winRound = Integer.parseInt(mainTd.get(3).html().replaceAll("<em(.*)<br>", "").trim());
            String minutesSecondsWinTime = mainTd.get(4).html().replaceAll("<em(.*)<br>", "").trim();
            Date date = MINUTES_SECONDS_DATE_FORMAT.parse(minutesSecondsWinTime);
            int winTime = date.getSeconds();

            mainFight.setWinMethod(winMethod);
            mainFight.setWinRound(winRound);
            mainFight.setWinTime(winTime);
        }

    }

    private Fight getMainFight(Event event, Elements mainFightElement) {
        Fight mainFight = new Fight();
        mainFight.setEvent(event);
        mainFight.setResult(ParserUtils.getFightResult(mainFightElement.first()));
        return mainFight;
    }

    private Fighter getFighterMainFight(Elements fighters, int number) {

        if (number != 1 && number != 2) {
            throw new IllegalArgumentException(
                    "Wrong fighter number, should be 1 or 2 (for first and second respectively)");
        }

        Fighter mainFighter = new Fighter();
        Element mainFighterElement = fighters.get(number - 1);
        mainFighter.setSherdogUrl(mainFighterElement.attr("abs:href"));
        mainFighter.setName(mainFighterElement.select("span[itemprop=\"name\"]").html());
        return mainFighter;
    }


    /**
     * Parses fights of an old event
     *
     * @param trs
     * @param event
     * @return
     */
    private List<Fight> parseEventFights(Elements trs, Event event) {

        List<Fight> fights = new ArrayList<>();

        if (trs.size() > 0) {
            trs.remove(0);

            trs.forEach(tr -> {
                Fight fight = new Fight();
                fight.setEvent(event);
                fight.setDate(event.getDate());
                Elements tds = tr.select(SELECTOR_TABLE_DATA);

                fight.setFighter1(parseFighterFromElements(tds.get(FIGHTER1_COLUMN)));
                fight.setFighter2(parseFighterFromElements(tds.get(FIGHTER2_COLUMN)));

                // Parse old fight, we can get the result
                if (tds.size() == 7) {
                    fight.setResult(parseWinResult(tds.get(FIGHTER1_COLUMN)));
                    fight.setWinMethod(parseWinMethod(tds.get(METHOD_COLUMN)));
                    fight.setWinRound(parseWinRound(tds.get(ROUND_COLUMN)));
                    fight.setWinTime(getTime(tds.get(TIME_COLUMN)));
                }

                fights.add(fight);
            });
        }

        return fights;
    }

    /**
     * Gets a fighter
     *
     * @param td element from Sherdog's table
     * @return return a Fighter with the fighter name and url
     */
    private Fighter parseFighterFromElements(Element td) {

        Elements nameElement = td.select(SELECTOR_FIGHTER_NAME);

        if (nameElement.size() > 0) {

            String name = nameElement.get(0).html();

            Elements select = td.select(SELECTOR_FIGHTER_DETAILS);

            if (select.size() > 0) {

                String url = select.get(0).attr(SELECTOR_FIGHTER_URL);
                Fighter fighter = new Fighter();
                fighter.setSherdogUrl(url);
                fighter.setName(name);
                return fighter;
            }
        }

        return null;
    }

    /**
     * Gets the time at which teh fight finished
     *
     * @param td element from Sherdog's table
     * @return get the time of the event
     */
    private int getTime(Element td) {

        try {
            Date date = MINUTES_SECONDS_DATE_FORMAT.parse(td.html());
            return date.getSeconds();

        } catch (ParseException e) {
            return 0;
        }

    }

    /**
     * Parses the round at which the even finished
     *
     * @param td element from Sherdog's table
     * @return the round number
     */
    private int parseWinRound(Element td) {
        return Integer.parseInt(td.html());
    }

    /**
     * Parses the win method of the event
     *
     * @param td element from Sherdog's table
     * @return get the win method
     */
    private WinMethod parseWinMethod(Element td) {
        return WinMethod.defineWinMethod(td.html().replaceAll("<br>(.*)", ""));
    }

    /**
     * Parses the result of the fight
     *
     * @param td element from Sherdog's table
     * @return a fight result enumerator value
     */
    private FightResult parseWinResult(Element td) {
        return ParserUtils.getFightResult(td);
    }

}
