package com.mmates.parsers.sherdog.promotions;

import com.mmates.core.model.Loadable;
import com.mmates.core.model.events.Event;
import com.mmates.core.model.promotion.Promotion;
import com.mmates.parsers.common.Parser;
import com.mmates.parsers.common.exceptions.ParserException;
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
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

import static com.mmates.parsers.sherdog.utils.SherdogConstants.SHERDOG_RECENT_EVENT_URL;
import static com.mmates.parsers.sherdog.utils.SherdogConstants.SHERDOG_TIME_ZONE;
import static java.lang.String.format;
import static java.time.ZoneId.systemDefault;
import static java.util.Comparator.comparing;
import static java.util.List.copyOf;

/**
 * Parse promotion from various Sherdog pages.
 *
 * @author Georgy Isaev
 * @version 1.0.0
 */
public class PromotionParser implements Parser<Promotion> {

    // Logger message
    private static final String MSG_INFO_RETRIEVE_PROMOTIONS_FROM_EVENT_PAGE = "Promotions from Events. " +
            "Parsing page [{}]";
    private static final String MSG_INFO_TEMPLATE_GETTING_NAME = "Getting name";
    private static final String MSG_INFO_TEMPLATE_GETTING_UPCOMING_EVENT = "Getting upcoming event";
    private static final String MSG_INFO_TEMPLATE_GETTING_PAST_EVENTS = "Getting past events";
    private static final String MSG_INFO_TEMPLATE_PARSING_PAGE = "Parsing page [{}]";
    private static final String MSG_INFO_TEMPLATE_PROCESSING_EVENT = "Processing event: [{}]";
    private static final String MSG_INFO_TEMPLATE_FOUNDED_PROMOTION = "Founded promotion: [{}]";
    private static final String MSG_INFO_TEMPLATE_FOUNDED_PROMOTIONS_OVERALL = "Founded promotions overall: [{}]";
    private static final String MSG_INFO_TEMPLATE_GETTING_RECENT_EVENTS = "Getting recent events";
    private static final String MSG_ERR_TEMPLATE_CANNOT_FORMAT_DATE = "Cannot format date, event will not be added";
    private static final String MSG_ERR_CANNOT_PARSE_SHERDOG_PAGE = "Cannot parse sherdog page";
    // Selectors
    private static final String SELECTOR_TABLE_DATA = "td";
    private static final String SELECTOR_RECENT_EVENTS_TABLE = "#recentfights_tab .event tr";
    private static final String SELECTOR_EVENT_NAME = ".bio_organization .module_header h2[itemprop=\"name\"]";
    private static final String SELECTOR_UPCOMING_EVENTS_TABLE = "#upcoming_tab .event tr";
    private static final String SELECTOR_EVENT_NAME_ELEMENT = "span[itemprop=\"name\"]";
    private static final String SELECTOR_EVENT_URL_ELEMENT = "a[itemprop=\"url\"]";
    private static final String SELECTOR_EVENT_DATE_ELEMENT = "meta[itemprop=\"startDate\"]";
    // Event table column numbers
    private static final int DATE_COLUMN = 0;
    private static final int NAME_COLUMN = 1;
    private static final int LOCATION_COLUMN = 2;
    // Logger
    private final Logger logger = LoggerFactory.getLogger(PromotionParser.class);
    // Fields
    private final ZoneId timeZone;

    // Constructors

    public PromotionParser() {
        this(systemDefault());
    }

    public PromotionParser(ZoneId zoneId) {
        this.timeZone = zoneId;
    }

    // Parsing logic

    /**
     * Retrieve promotions from recent event page.
     *
     * @param start a first event page for parsing
     * @param depth a number of pages to parsing
     * @return the promotion list
     */
    public List<Promotion> retrievePromotionsFromRecentEventPage(final int start, final int depth) throws InterruptedException {
        Map<String, Promotion> foundedPromotions = new ConcurrentHashMap<>();
        ExecutorService service = null;
        try {
            service = Executors.newCachedThreadPool();
            int currentPageNumber = start;
            logger.info(MSG_INFO_TEMPLATE_GETTING_RECENT_EVENTS);
            do {
                logger.info(MSG_INFO_RETRIEVE_PROMOTIONS_FROM_EVENT_PAGE, currentPageNumber);
                final int pageNumber = currentPageNumber;
                service.submit(getExtractionPromotionsTask(foundedPromotions, pageNumber));
                currentPageNumber++;
                logger.info(MSG_INFO_TEMPLATE_FOUNDED_PROMOTIONS_OVERALL, foundedPromotions.size());
            } while (currentPageNumber < start + depth);
        } finally {
            if (service != null) {
                service.shutdown();
            }
        }
        boolean finished = service.awaitTermination(1, TimeUnit.HOURS);
        return finished ? copyOf(foundedPromotions.values()) : new ArrayList<>();
    }

    private Runnable getExtractionPromotionsTask(Map<String, Promotion> foundedPromotions, final int pageNumber) {
        return () -> {
            try {
                Document document = ParserUtils.parseDocument(format(SHERDOG_RECENT_EVENT_URL, pageNumber));
                List<Promotion> promotionsToAdd =
                        parsePromotionsFromEvent(document.select(SELECTOR_RECENT_EVENTS_TABLE));
                promotionsToAdd.forEach(promotion -> {
                    foundedPromotions.putIfAbsent(promotion.getSherdogUrl(), promotion);
                    logger.info(MSG_INFO_TEMPLATE_FOUNDED_PROMOTION, promotion.getName());
                });
            } catch (IOException e) {
                logger.info(MSG_ERR_CANNOT_PARSE_SHERDOG_PAGE, e);
            }
        };
    }

    /**
     * Retrieves the promotion list from event table rows.
     *
     * @param tableRows the JSOUP TR elements from the event table
     * @return a list of events
     */
    private List<Promotion> parsePromotionsFromEvent(Elements tableRows) {
        Set<Promotion> promotions = new ConcurrentSkipListSet<>(comparing(Loadable::getSherdogUrl));
        final Sherdog sherdog = new Sherdog.Builder()
                .withTimezone(SHERDOG_TIME_ZONE)
                .build();
        if (!tableRows.isEmpty()) {
            tableRows.remove(0);
            tableRows.forEach(tableRow -> {
                Elements eventDetails = tableRow.select(SELECTOR_TABLE_DATA);
                String eventUrl = parseEventUrl(eventDetails.get(NAME_COLUMN));
                extractPromotion(promotions, sherdog, eventUrl);
            });
        }
        return new ArrayList<>(promotions);
    }

    private void extractPromotion(Set<Promotion> promotions, Sherdog sherdog, String eventUrl) {
        try {
            Event event = sherdog.parseEventFromUrl(eventUrl);
            logger.info(MSG_INFO_TEMPLATE_PROCESSING_EVENT, event);
            Promotion promotion = event.getPromotion();
            if (promotion != null) {
                promotions.add(promotion);
            }
        } catch (IOException | ParseException | ParserException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * Parses a promotion page into a {@link Promotion} object.
     *
     * @param document Jsoup document of the sherdog page
     * @throws IOException    if connecting to sherdog fails
     * @throws ParseException if the page structure has changed
     */
    @Override
    public Promotion parseDocument(Document document) throws IOException, ParseException {

        Promotion promotion = new Promotion();
        promotion.setSherdogUrl(SherdogParserUtils.retrieveSherdogPageUrl(document));
        String url = promotion.getSherdogUrl();
        url += "/recent-events/%d";
        int page = 1;
        document = ParserUtils.parseDocument(format(url, page));
        logger.info(MSG_INFO_TEMPLATE_GETTING_NAME);
        Elements name = document.select(SELECTOR_EVENT_NAME);
        promotion.setName(name.html());
        logger.info(MSG_INFO_TEMPLATE_GETTING_UPCOMING_EVENT);
        Elements upcomingEventsElement = document.select(SELECTOR_UPCOMING_EVENTS_TABLE);
        promotion.getEvents().addAll(parseEvent(upcomingEventsElement, promotion));
        logger.info(MSG_INFO_TEMPLATE_GETTING_PAST_EVENTS);
        List<Event> toAdd;
        do {
            logger.info(MSG_INFO_TEMPLATE_PARSING_PAGE, page);
            document = ParserUtils.parseDocument(format(url, page));
            Elements events = document.select("#recent_tab .event tr");
            toAdd = parseEvent(events, promotion);
            promotion.getEvents().addAll(toAdd);
            page++;
        } while (!toAdd.isEmpty());
        promotion.getEvents().sort(comparing(Event::getDate));
        return promotion;
    }

    /**
     * Retrieve all promotion events
     *
     * @param trs the Jsoup TR elements from the event table
     * @return a list of events
     */
    private List<Event> parseEvent(Elements trs, Promotion promotion) {

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
                    logger.error(MSG_ERR_TEMPLATE_CANNOT_FORMAT_DATE, e);
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
        Elements url = td.select(SELECTOR_EVENT_URL_ELEMENT);
        if (url.size() > 0) {
            return url.get(0).attr("abs:href");
        } else {
            return "";
        }
    }

    private ZonedDateTime parseEventDate(Element element) {
        Elements metaDate = element.select(SELECTOR_EVENT_DATE_ELEMENT);
        if (metaDate.size() > 0) {
            String date = metaDate.get(0).attr("content");
            return ParserUtils.convertStringToZonedDate(date, timeZone);
        } else {
            return null;
        }
    }

}
