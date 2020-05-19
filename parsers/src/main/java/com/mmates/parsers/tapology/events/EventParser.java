package com.mmates.parsers.tapology.events;

import com.mmates.core.model.events.Event;
import com.mmates.core.model.fights.Fight;
import com.mmates.core.model.fights.FightResult;
import com.mmates.core.model.fights.WinMethod;
import com.mmates.core.model.people.Fighter;
import com.mmates.core.model.promotion.Promotion;
import com.mmates.parsers.common.Parser;
import com.mmates.parsers.common.exceptions.NotParserSourceURLException;
import com.mmates.parsers.common.exceptions.ParserException;
import com.mmates.parsers.common.utils.Constants;
import com.mmates.parsers.common.utils.ParserUtils;
import com.mmates.parsers.tapology.Tapology;
import com.mmates.parsers.tapology.utils.TapologyConstants;
import com.mmates.parsers.tapology.utils.TapologyParserUtils;
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
    public static final String SELECTOR_TABLE_DATA = "td";

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
     * Parse a page
     *
     * @param url of the site page
     * @return the object parsed by the parser
     * @throws IOException            if connecting to Sherdog fails
     * @throws ParseException         if the page structure has changed
     * @throws ParserException if anything related to the parser goes wrong
     */
    @Override
    public Event parse(String url) throws IOException, ParseException, ParserException {
        if (!url.startsWith("https")) {
            url = url.replace("http", "https");
        }
        if (!url.startsWith(TapologyConstants.BASE_HTTPS_URL)) {
            throw new NotParserSourceURLException();
        }

        Document doc = ParserUtils.parseDocument(url);
        return parseDocument(doc);
    }

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
        Tapology sherdog = new Tapology.Builder().withTimezone(Constants.SHERDOG_TIME_ZONE).build();
        sherdog.setFastMode(isFastMode());

        logger.info("Getting recent events {} ", String.format(url, currentPageNumber));

        do {

            Document doc = ParserUtils.parseDocument(String.format(url, currentPageNumber));
            Elements eventElements = doc.select("#recentfights_tab .event tr");

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

        event.setSherdogUrl(TapologyParserUtils.getSherdogPageUrl(doc));

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

        Elements date = doc.select(".right .clearfix .header");
        // TODO: get date to proper format
        try {
            event.setDate(TapologyParserUtils.getDateFromStringToZoneId(date.first().getElementsByTag("li").text(), ZONE_ID));
        } catch (DateTimeParseException error) {
            logger.error("Couldn't parse date", error);
        }
    }

    private void parseDocumentPromotion(Document doc, Event event) {

        Elements elements = doc.select(".header .section_title h2 a");
        if (elements.size() == 0) {
            // Promotion is not in Sherdog database.
            return;
        }
        Element org = elements.get(0);
        Promotion promotion = new Promotion();
        promotion.setSherdogUrl(org.attr("abs:href"));
        promotion.setName(org.select("span[itemprop=\"name\"]").get(0).html());

        event.setPromotion(promotion);
    }

    private void parseEventLocation(Document doc, Event event) {

        if (isFastMode())
            return;

        Elements location = doc.select("span[itemprop=\"location\"]");
        event.setLocation(location.html().replace("<br>", " - "));
    }

    private void parseEventName(Document doc, Event event) {

        Elements name = doc.select(".eventPageHeaderTitles h1");
        event.setName(name.html());

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
        Elements mainFightElement = doc.select(".content.event");

        Elements fighters = mainFightElement.select("h3 a");

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

        Elements tds = doc.select(".event_match table tr");

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

        Elements name1 = td.select("span[itemprop=\"name\"]");

        if (name1.size() > 0) {

            String name = name1.get(0).html();

            Elements select = td.select("a[itemprop=\"url\"]");

            if (select.size() > 0) {
                String url = select.get(0).attr("abs:href");

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
