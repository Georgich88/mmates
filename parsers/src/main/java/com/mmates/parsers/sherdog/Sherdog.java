package com.mmates.parsers.sherdog;

import com.mmates.core.model.events.Event;
import com.mmates.core.model.people.Fighter;
import com.mmates.core.model.promotion.Promotion;
import com.mmates.parsers.common.exceptions.ParserException;
import com.mmates.parsers.common.utils.Constants;
import com.mmates.parsers.common.utils.ParserUtils;
import com.mmates.parsers.common.utils.PictureProcessor;
import com.mmates.parsers.sherdog.events.EventParser;
import com.mmates.parsers.sherdog.people.FighterParser;
import com.mmates.parsers.sherdog.promotions.PromotionParser;
import com.mmates.parsers.sherdog.searches.Search;
import com.mmates.parsers.sherdog.utils.SherdogConstants;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.mmates.parsers.sherdog.utils.SherdogConstants.SELECTOR_TABLE_DATA;
import static com.mmates.parsers.sherdog.utils.SherdogConstants.SHERDOG_RECENT_EVENT_URL_TEMPLATE;
import static com.mmates.parsers.sherdog.utils.SherdogParserUtils.parseEventUrl;
import static java.lang.String.format;

public class Sherdog {

    private static final Logger logger = LoggerFactory.getLogger(Sherdog.class);

    // Cache

    private Map<String, Event> eventCache = Collections.synchronizedMap(new WeakHashMap<>());
    private Map<String, Event> promotionCache = Collections.synchronizedMap(new WeakHashMap<>());
    private Map<String, Event> fightCache = Collections.synchronizedMap(new WeakHashMap<>());

    private ZoneId timeZone = ZoneId.systemDefault();
    private PictureProcessor pictureProcessor = Constants.DEFAULT_PICTURE_PROCESSOR;

    /**
     * Prepares the search for fighters and events
     *
     * @param query the search query
     * @return a Search object that can be augmented with things like weight class
     */
    public Search search(String query) {
        return new Search(query, this);
    }

    /**
     * Gets the zone id
     *
     * @return the current zoneid
     */
    public ZoneId getTimeZone() {
        return timeZone;
    }

    /**
     * Sets the zoneod
     *
     * @param timeZone which zone id the event times need to be converted
     */
    public void setTimeZone(ZoneId timeZone) {
        this.timeZone = timeZone;
    }

    /**
     * Parses recent events from the recent event page.
     *
     * @param start the start position
     * @param depth amount of events to parse
     * @return recent events
     * @throws IOException
     * @throws ParserException
     */
    public List<Event> parseRecentEvents(int start, int depth) throws IOException {
        final Queue<Event> events = new ConcurrentLinkedQueue<Event>();
        int currentPageNumber = start;
        logger.info(SherdogConstants.MSG_INFO_GETTING_RECENT_EVENTS, format(SHERDOG_RECENT_EVENT_URL_TEMPLATE,
                currentPageNumber));
        ExecutorService executorService = Executors.newCachedThreadPool();
        do {
            int finalCurrentPageNumber = currentPageNumber;
            executorService.submit(() -> parseRecentEventOnPage(events, finalCurrentPageNumber));
            currentPageNumber++;
        } while (currentPageNumber < start + depth);

        return new ArrayList<>(events);
    }

    private void parseRecentEventOnPage(Queue<Event> events, int currentPageNumber) {
        try {
            Document document = ParserUtils.parseDocument(format(SHERDOG_RECENT_EVENT_URL_TEMPLATE, currentPageNumber));
            Elements eventElements = document.select(SherdogConstants.SELECTOR_RECENT_EVENTS_TABLE);
            if (!eventElements.isEmpty()) {
                eventElements.remove(0);
                eventElements.forEach(tr -> {
                    Elements tds = tr.select(SELECTOR_TABLE_DATA);
                    String eventUrl = parseEventUrl(tds.get(1));
                    Event event = null;
                    try {
                        event = parseEventFromUrl(eventUrl);
                    } catch (IOException | ParseException | ParserException e) {
                        logger.error("", e);
                    }
                    if (event != null) {
                        events.add(event);
                    }
                });
            }
        } catch (Exception e) {
            logger.error("", e);
        }

    }

    /**
     * Gets an organization via it's Sherdog URL.
     *
     * @param sherdogUrl Sherdog URL, can find predefined url in Promotions.* enum.
     * @return an Promotion
     * @throws IOException     if connecting to sherdog fails
     * @throws ParseException  if the page structure has changed
     * @throws ParserException if anythign related to the parser goes wrong
     */
    public Promotion parsePromotion(String sherdogUrl) throws IOException, ParseException, ParserException {
        return new PromotionParser(timeZone).parse(sherdogUrl);
    }

    /**
     * Gets an organization via it's sherdog page HTML, in case you want to have
     * your own way of getting teh HTML content
     *
     * @param html The web page HTML
     * @return an Promotion
     * @throws IOException     if connecting to sherdog fails
     * @throws ParseException  if the page structure has changed
     * @throws ParserException if anythign related to the parser goes wrong
     */
    public Promotion parsePromotionFromHtml(String html) throws IOException, ParseException, ParserException {
        return new PromotionParser(timeZone).parseFromHtml(html);
    }

    /**
     * Gets an organization via it's sherdog URL.
     *
     * @param promotion A promotion from the Promotions. enum
     * @return an Promotion
     * @throws IOException     if connecting to sherdog fails
     * @throws ParseException  if the page structure has changed
     * @throws ParserException if anythign related to the parser goes wrong
     */
    public Promotion parsePromotion(Promotion promotion) throws IOException, ParseException, ParserException {
        return new PromotionParser(timeZone).parse(promotion.getSherdogUrl());
    }

    /**
     * Gets an event via it's shergog page HTML
     *
     * @param html The web page HTML
     * @return an Event
     * @throws IOException     if connecting to sherdog fails
     * @throws ParseException  if the page structure has changed
     * @throws ParserException if anythign related to the parser goes wrong
     */
    public Event parseEventFromHtml(String html) throws IOException, ParseException, ParserException {
        return new EventParser(timeZone).parseFromHtml(html);
    }

    /**
     * Gets an event via it's sherdog URL.
     *
     * @param sherdogUrl Sherdog URL, can be found in the list of event of an
     *                   organization
     * @return an Event
     * @throws IOException     if connecting to sherdog fails
     * @throws ParseException  if the page structure has changed
     * @throws ParserException if anythign related to the parser goes wrong
     */
    public Event parseEventFromUrl(String sherdogUrl) throws IOException, ParseException, ParserException {
        EventParser eventParser = new EventParser(timeZone);
        return eventParser.parse(sherdogUrl);
    }

    /**
     * Get a fighter via it;s sherdog page HTML
     *
     * @param html The web page HTML
     * @return a Fighter an all his fights
     * @throws IOException     if connecting to sherdog fails
     * @throws ParseException  if the page structure has changed
     * @throws ParserException if anythign related to the parser goes wrong
     */
    public Fighter parseFighterFromHtml(String html) throws IOException, ParseException, ParserException {
        return new FighterParser(pictureProcessor, timeZone).parseFromHtml(html);
    }

    /**
     * Parse a fighter via its sherdog URL.
     *
     * @param sherdogUrl the Sherdog url of the fighter
     * @return a Fighter an all his fights
     * @throws IOException     if connecting to sherdog fails
     * @throws ParseException  if the page structure has changed
     * @throws ParserException if anything related to the parser goes wrong
     */
    public Fighter parseFighterFromUrl(String sherdogUrl) throws IOException, ParserException, ParseException {
        return new FighterParser(pictureProcessor, timeZone).parse(sherdogUrl);
    }

    /**
     * Gets a picture processor
     *
     * @return
     */
    public PictureProcessor getPictureProcessor() {
        return pictureProcessor;
    }

    /**
     * Sets a picture processor if some processing is needed for the fighter picture
     *
     * @param pictureProcessor the picture processor to use
     */
    public void setPictureProcessor(PictureProcessor pictureProcessor) {
        this.pictureProcessor = pictureProcessor;
    }

    // Builder

    public static class Builder {

        private final Sherdog parser;

        public Builder() {
            parser = new Sherdog();
        }

        // Setters

        public Builder withPictureProcessor(PictureProcessor processor) {
            parser.setPictureProcessor(processor);
            return this;
        }

        public Builder withTimezone(String timezone) {
            parser.setTimeZone(ZoneId.of(timezone));
            return this;
        }

        public Sherdog build() {
            return parser;
        }

    }

}
