package com.mmates.parsers.sherdog.promotions;


import com.mmates.core.model.events.Event;
import com.mmates.core.model.promotion.Promotion;
import com.mmates.parsers.common.Parser;
import com.mmates.parsers.common.exceptions.ParserException;
import com.mmates.parsers.common.utils.Constants;
import com.mmates.parsers.common.utils.ParserUtils;
import com.mmates.parsers.sherdog.Sherdog;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PromotionParser implements Parser<Promotion> {

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

    /**
     * Creates an promotion parser with the default zone id
     */
    public PromotionParser() {
        ZONE_ID = ZoneId.systemDefault();
    }

    /**
     * Create a parser with a specified zoneid
     *
     * @param zoneId specified zone id for time conversion
     */
    public PromotionParser(ZoneId zoneId) {
        this.ZONE_ID = zoneId;
    }

    public List<Promotion> downloadPromotions(int start, int depth)
            throws IOException, ParseException, ParserException {

        List<Promotion> promotions = new ArrayList<Promotion>();
        Map<String, Promotion> foundedPromotions = new HashMap<String, Promotion>();

        String url = "https://www.sherdog.com/events/recent/%d-page/";
        int currentPageNumber = start;
        Document doc = ParserUtils.parseDocument(String.format(url, currentPageNumber));

        logger.info("Getting recent events");
        List<Promotion> promotionsToAdd;
        do {
            // logger.info("Promotions from Events. Parsing page [{}]", currentPageNumber);

            doc = ParserUtils.parseDocument(String.format(url, currentPageNumber));
            Elements events = doc.select("#recentfights_tab .event tr");
            promotionsToAdd = parsePromotionsFromEvent(events);

            for (Promotion toAdd : promotionsToAdd) {
                foundedPromotions.putIfAbsent(toAdd.getSherdogUrl(), toAdd);
                logger.info("Founded promotion: [{}]", toAdd.getName());
            }

            currentPageNumber++;
            logger.info("Founded promotions overall: [{}]", foundedPromotions.size());

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

        List<Promotion> promotions = new ArrayList<Promotion>();
        Sherdog sherdog = new Sherdog.Builder().withTimezone(Constants.SHERDOG_TIME_ZONE).build();
        sherdog.setFastMode(isFastMode());

        if (trs.size() > 0) {
            trs.remove(0);

            trs.forEach(tr -> {

                Elements tds = tr.select("td");

                String eventUrl = getEventUrl(tds.get(NAME_COLUMN));

                try {

                    Event event = (Event) sherdog.getEvent(eventUrl);
                    logger.info("Processing event: [{}]", event);
                    Promotion promotion = (Promotion) event.getPromotion();
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
     * Parse a Sherdog page
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

        logger.info("Getting name");
        Elements name = doc.select(".bio_organization .module_header h2[itemprop=\"name\"]");
        promotion.setName(name.html());

        logger.info("Getting upcoming event");
        Elements upcomingEventsElement = doc.select("#upcoming_tab .event tr");
        promotion.getEvents().addAll(parseEvent(upcomingEventsElement, promotion));

        logger.info("Getting past events");
        List<Event> toAdd;
        do {
            logger.info("Parsing page [{}]", page);

            doc = ParserUtils.parseDocument(String.format(url, page));
            Elements events = doc.select("#recent_tab .event tr");

            toAdd = parseEvent(events, promotion);

            promotion.getEvents().addAll(toAdd);
            page++;

        } while (toAdd.size() > 0);

        promotion.getEvents().sort((o1, o2) -> o1.getDate().compareTo(o2.getDate()));
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
                Elements tds = tr.select("td");

                event.setPromotion(sOrg);

                event.setName(getEventName(tds.get(NAME_COLUMN)));
                event.setSherdogUrl(getEventUrl(tds.get(NAME_COLUMN)));
                event.setLocation(getElementLocation(tds.get(LOCATION_COLUMN)));

                try {
                    event.setDate(getEventDate(tds.get(DATE_COLUMN)));
                } catch (DateTimeParseException e) {
                    logger.error("Couldn't fornat date, we shouldn't add the event", e);
                    addEvent = false;
                }

                if (addEvent) {
                    events.add(event);
                }
            });
        }

        return events;
    }

    private String getElementLocation(Element td) {
        String[] split = td.html().split(">");
        if (split.length > 1) {
            return split[1].trim();
        } else {
            return "";
        }
    }

    private String getEventName(Element td) {
        Elements nameElement = td.select("span[itemprop=\"name\"]");

        if (nameElement.size() > 0) {
            String name = nameElement.get(0).html();
            name = name.replaceAll("( )+", " ").trim();
            return name;
        } else {
            return "";
        }
    }

    private String getEventUrl(Element td) {
        Elements url = td.select("a[itemprop=\"url\"]");
        if (url.size() > 0) {
            String attr = url.get(0).attr("abs:href");
            return attr;
        } else {
            return "";
        }
    }

    private ZonedDateTime getEventDate(Element element) {
        Elements metaDate = element.select("meta[itemprop=\"startDate\"]");
        if (metaDate.size() > 0) {
            String date = metaDate.get(0).attr("content");

            return ParserUtils.getDateFromStringToZoneId(date, ZONE_ID);
        } else {
            return null;
        }
    }
}
