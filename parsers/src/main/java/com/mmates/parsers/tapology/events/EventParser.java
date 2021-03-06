package com.mmates.parsers.tapology.events;

import com.mmates.core.model.events.Event;
import com.mmates.core.model.fights.Fight;
import com.mmates.core.model.fights.FightResult;
import com.mmates.core.model.fights.WinMethod;
import com.mmates.core.model.people.Fighter;
import com.mmates.core.model.sources.SourceInformation;
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
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

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
    public static final String CSS_QUERY_SELECTOR_EVENT_NAME = ".eventPageHeaderContainer .eventPageHeaderTitles h1";

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
     * @throws IOException     if connecting to Sherdog fails
     * @throws ParseException  if the page structure has changed
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
        if (!url.isEmpty()) {
            return url.get(0).attr("abs:href");
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
        event.setName(doc.select(CSS_QUERY_SELECTOR_EVENT_NAME).text());
        event.setTapologyUrl(TapologyParserUtils.getTapologyPageUrl(doc));
        parseEventAttributes(doc, event);
        retrieveFights(doc, event);

        return event;
    }

    private void parseEventAttributes(Document doc, Event event) {

        Elements eventProperties = doc.select(".right .clearfix li");
        if (eventProperties.size() == 0) {
            logger.error("Couldn't parse event attributes");
        }

        eventProperties.forEach(property -> {
            Elements attributes = property.children();
            attributes.forEach(attribute -> {
                extractEventProperty(event, attribute, "Location", Elements::text, String::intern, event::setLocation);
                extractEventProperty(event, attribute, "Venue", Elements::text, String::intern, event::setVenue);
                extractEventProperty(event, attribute, "Enclosure", Elements::text, String::intern, event::setEnclosure);
                extractEventProperty(event, attribute, "Location", Elements::text, String::intern, event::setLocation);
                extractEventProperty(event, attribute, "Ownership", Elements::text, String::intern, event::setOwnership);
                extractEventLinks(event, attribute, "Event Links", this::getSourceInformationMapper, map -> map, event::addProfiles);

            });
        });

        // TODO: get date to proper format
        try {
            Elements date = doc.select(".right .clearfix .header");
            event.setDate(TapologyParserUtils.getDateFromStringToZoneId(date.first().getElementsByTag("li").text(), ZONE_ID));
        } catch (DateTimeParseException error) {
            logger.error("Couldn't parse date", error);
        }
    }

    private HashMap<SourceInformation, String> getSourceInformationMapper(Elements elements) {
        HashMap<SourceInformation, String> map = new HashMap<>();
        elements.forEach(element -> {
            Elements links = element.getElementsByAttribute("href");
            links.forEach(link -> {
                String url = link.attr("href");
                SourceInformation source = SourceInformation.defineSourceByUrl(url);
                if (!url.isEmpty() && source != null) {
                    map.put(source, url);
                }
            });
        });
        return map;
    }

    private <T, U> void extractEventProperty(final Event event, final Element element, final String propertyName,
                                             final Function<Elements, U> extractor, final Function<U, T> mapper, final Consumer<T> setter) {
        Elements labels = element.getElementsMatchingText(propertyName);
        Elements values = labels.next();
        if (values.size() > 0) {
            final U text = extractor.apply(values);
            T value = mapper.apply(text);
            setter.accept(value);
        }
    }

    private <T, U> void extractEventLinks(final Event event, final Element element, final String propertyName,
                                          final Function<Elements, U> extractor, final Function<U, T> mapper, final Consumer<T> setter) {
        Elements labels = element.getElementsMatchingText(propertyName);
        Elements values = labels.nextAll();
        if (values.size() > 0) {
            final U text = extractor.apply(values);
            T value = mapper.apply(text);
            setter.accept(value);
        }
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

        Elements eventFightCard = doc.select(".fightCard .fightCard .fightCardBout");
        if (eventFightCard.size() == 0) {
            logger.error("Couldn't parse an event bout");
        }

        eventFightCard.forEach(fightCardElement -> {
            // Check if it's a prediction
            if (fightCardElement.select(".eventBoutPicks").size() > 0) return;

        });
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
        mainFight.setResult(ParserUtils.parseFightResult(mainFightElement.first()));
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

                fight.setFirstFighter(parseFighterFromElements(tds.get(FIGHTER1_COLUMN)));
                fight.setSecondFighter(parseFighterFromElements(tds.get(FIGHTER2_COLUMN)));

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
        return ParserUtils.parseFightResult(td);
    }

}
