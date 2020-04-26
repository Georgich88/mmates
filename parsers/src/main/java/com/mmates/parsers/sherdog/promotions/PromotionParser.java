package com.mmates.parsers.sherdog.promotions;


import com.mmates.core.model.events.Event;
import com.mmates.core.model.promotion.Promotion;
import com.mmates.parsers.common.Parser;
import com.mmates.parsers.common.exceptions.ParserException;
import com.mmates.parsers.common.utils.ParserUtils;
import com.mmates.parsers.sherdog.Sherdog;
import com.mmates.parsers.sherdog.utils.SherdogConstants;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;


public class PromotionParser implements Parser<Promotion> {

    public static final String MESSAGE_INFO_TEMPLATE_GETTING_NAME = "Getting name";
    public static final String MESSAGE_INFO_TEMPLATE_GETTING_UPCOMING_EVENT = "Getting upcoming event";
    public static final String MESSAGE_INFO_TEMPLATE_GETTING_PAST_EVENTS = "Getting past events";
    public static final String MESSAGE_INFO_TEMPLATE_PARSING_PAGE = "Parsing page [{}]";
    public static final String MESSAGE_INFO_TEMPLATE_PROCESSING_EVENT = "Processing event: [{}]";
    public static final String MESSAGE_ERROR_TEMPLATE_CANNOT_FORMAT_DATE = "Cannot format date, event will not be added";
    public static final String MESSAGE_INFO_TEMPLATE_FOUNDED_PROMOTION = "Founded promotion: [{}]";
    public static final String MESSAGE_INFO_TEMPLATE_FOUNDED_PROMOTIONS_OVERALL = "Founded promotions overall: [{}]";

    public static final String SELECTOR_TABLE_DATA = "td";
    public static final String SELECTOR_RECENT_EVENTS_TABLE = "#recentfights_tab .event tr";
    public static final String SELECTOR_EVENT_NAME = ".bio_organization .module_header h2[itemprop=\"name\"]";
    public static final String SELECTOR_UPCOMING_EVENTS_TABLE = "#upcoming_tab .event tr";
    public static final String SELECTOR_EVENT_NAME_ELEMENT = "span[itemprop=\"name\"]";

    private final Logger logger = LoggerFactory.getLogger(PromotionParser.class);

    private final int DATE_COLUMN = 0;
    private final int NAME_COLUMN = 1;
    private final int LOCATION_COLUMN = 2;
    private final ZoneId ZONE_ID;
    private boolean fastMode = false;

    public boolean isFastMode() {
        return fastMode;
    }

    public void setFastMode(boolean fastMode) {
        this.fastMode = fastMode;
    }

    // Constructors

    /**
     * Creates an promotion parser with the default Zone id
     */
    public PromotionParser() {
        ZONE_ID = ZoneId.systemDefault();
    }

    /**
     * Create a parser with a specified Zone id
     *
     * @param zoneId specified zone id for time conversion
     */
    public PromotionParser(ZoneId zoneId) {
        this.ZONE_ID = zoneId;
    }

    // Parsing logic

    public List<Promotion> downloadPromotions(int start, int depth)
            throws IOException, ParseException, ParserException {

        List<Promotion> promotions = new ArrayList<>();
        Map<String, Promotion> foundedPromotions = new HashMap<>();

        String url = SherdogConstants.SHERDOG_RECENT_EVENT_URL;
        int currentPageNumber = start;
        ParserUtils.parseDocument(String.format(url, currentPageNumber));
        Document doc;

        logger.info("Getting recent events");
        List<Promotion> promotionsToAdd;
        do {
            // logger.info("Promotions from Events. Parsing page [{}]", currentPageNumber);

            doc = ParserUtils.parseDocument(String.format(url, currentPageNumber));
            Elements events = doc.select(SELECTOR_RECENT_EVENTS_TABLE);
            promotionsToAdd = parsePromotionsFromEvent(events);

            for (Promotion toAdd : promotionsToAdd) {
                foundedPromotions.putIfAbsent(toAdd.getSherdogUrl(), toAdd);
                logger.info(MESSAGE_INFO_TEMPLATE_FOUNDED_PROMOTION, toAdd.getName());
            }

            currentPageNumber++;
            logger.info(MESSAGE_INFO_TEMPLATE_FOUNDED_PROMOTIONS_OVERALL, foundedPromotions.size());

        } while (promotionsToAdd.size() > 0 && currentPageNumber < start + depth);

        foundedPromotions.forEach((k, v) -> {
            promotions.add(v);
        });

        return promotions;
    }

    /**
     * List of events' promotions
     *
     * @param trs the JSOUP TR elements from the event table
     * @return a list of events
     * @throws ParseException if something is wrong with sherdog layout
     */
    private List<Promotion> parsePromotionsFromEvent(Elements trs)
            throws IOException, ParseException, ParserException {

        List<Promotion> promotions = new ArrayList<>();
        Sherdog sherdog = new Sherdog.Builder()
                .withTimezone(SherdogConstants.SHERDOG_TIME_ZONE)
                .build();
        sherdog.setFastMode(isFastMode());

        if (trs.size() > 0) {
            trs.remove(0);

            trs.forEach(tr -> {

                Elements tds = tr.select(SELECTOR_TABLE_DATA);
                String eventUrl = parseEventUrl(tds.get(NAME_COLUMN));

                try {

                    Event event = sherdog.getEvent(eventUrl);
                    logger.info(MESSAGE_INFO_TEMPLATE_PROCESSING_EVENT, event);
                    Promotion promotion = event.getPromotion();
                    if (promotion != null) {
                        promotions.add(promotion);
                    }
                } catch (IOException err) {
                    logger.error(err.getMessage());
                    err.printStackTrace();
                } catch (ParseException err) {
                    logger.error(err.getMessage());
                    err.printStackTrace();
                } catch (ParserException err) {
                    logger.error(err.getMessage());
                    err.printStackTrace();
                }

            });
        }

        return promotions;
    }

    /**
     * Parses a page
     *
     * @param doc Jsoup document of the sherdog page
     * @throws IOException    if connecting to sherdog fails
     * @throws ParseException if the page structure has changed
     */
    public Promotion parseDocument(Document doc) throws IOException, ParseException {

        Promotion promotion = new Promotion();
        promotion.setSherdogUrl(ParserUtils.getSherdogPageUrl(doc));

        String url = promotion.getSherdogUrl();
        url += "/recent-events/%d";
        int page = 1;

        doc = ParserUtils.parseDocument(String.format(url, page));

        logger.info(MESSAGE_INFO_TEMPLATE_GETTING_NAME);
        Elements name = doc.select(SELECTOR_EVENT_NAME);
        promotion.setName(name.html());

        logger.info(MESSAGE_INFO_TEMPLATE_GETTING_UPCOMING_EVENT);
        Elements upcomingEventsElement = doc.select(SELECTOR_UPCOMING_EVENTS_TABLE);
        promotion.getEvents().addAll(parseEvent(upcomingEventsElement, promotion));

        logger.info(MESSAGE_INFO_TEMPLATE_GETTING_PAST_EVENTS);
        List<Event> toAdd;
        do {

            logger.info(MESSAGE_INFO_TEMPLATE_PARSING_PAGE, page);

            doc = ParserUtils.parseDocument(String.format(url, page));
            Elements events = doc.select("#recent_tab .event tr");

            toAdd = parseEvent(events, promotion);

            promotion.getEvents().addAll(toAdd);
            page++;

        } while (toAdd.size() > 0);

        promotion.getEvents().sort(Comparator.comparing(Event::getDate));
        return promotion;
    }

    /**
     * Get all the events of an promotion
     *
     * @param trs the Jsoup TR elements from the event table
     * @return a list of events
     * @throws ParseException if something is wrong with sherdog layout
     */
    private List<Event> parseEvent(Elements trs, Promotion promotion) throws ParseException {
        List<Event> events = new ArrayList<>();

        if (trs.size() > 0) {
            trs.remove(0);

            Promotion sOrg = new Promotion();
            sOrg.setName(promotion.getName());
            sOrg.setSherdogUrl(promotion.getSherdogUrl());

            trs.forEach(tr -> {

                Event event = new Event();
                boolean addEvent = true;
                Elements tds = tr.select(SELECTOR_TABLE_DATA);

                event.setPromotion(sOrg);
                event.setName(parseEventName(tds.get(NAME_COLUMN)));
                event.setSherdogUrl(parseEventUrl(tds.get(NAME_COLUMN)));
                event.setLocation(parseElementLocation(tds.get(LOCATION_COLUMN)));

                try {
                    event.setDate(parseEventDate(tds.get(DATE_COLUMN)));
                } catch (DateTimeParseException e) {
                    logger.error(MESSAGE_ERROR_TEMPLATE_CANNOT_FORMAT_DATE, e);
                    addEvent = false;
                }

                if (addEvent) {
                    events.add(event);
                }
            });
        }

        return events;
    }

    private String parseElementLocation(Element td) {
        String[] split = td.html().split(">");
        if (split.length > 1) {
            return split[1].trim();
        } else {
            return "";
        }
    }

    private String parseEventName(Element td) {

        Elements nameElement = td.select(SELECTOR_EVENT_NAME_ELEMENT);
        if (nameElement.size() > 0) {
            String name = nameElement.get(0).html();
            name = name.replaceAll("( )+", " ").trim();
            return name;
        } else {
            return "";
        }
    }

    private String parseEventUrl(Element td) {
        Elements url = td.select("a[itemprop=\"url\"]");
        if (url.size() > 0) {
            String attr = url.get(0).attr("abs:href");
            return attr;
        } else {
            return "";
        }
    }

    private ZonedDateTime parseEventDate(Element element) {
        Elements metaDate = element.select("meta[itemprop=\"startDate\"]");
        if (metaDate.size() > 0) {
            String date = metaDate.get(0).attr("content");

            return ParserUtils.getDateFromStringToZoneId(date, ZONE_ID);
        } else {
            return null;
        }
    }
}
