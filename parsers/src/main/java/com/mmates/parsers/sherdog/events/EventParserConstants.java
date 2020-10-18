package com.mmates.parsers.sherdog.events;

import java.text.SimpleDateFormat;

public final class EventParserConstants {

    // Selectors
    static final String SELECTOR_PROMOTION_NAME = "span[itemprop=\"name\"]";
    static final String SELECTOR_PROMOTION_URL = "abs:href";
    static final String SELECTOR_PROMOTION = ".header .section_title h2 a";
    static final String SELECTOR_LOCATION = "span[itemprop=\"location\"]";
    static final String SELECTOR_EVENT_NAME = ".header .section_title h1 span[itemprop=\"name\"]";
    static final String SELECTOR_MAIN_FIGHT = ".content.event";
    static final String SELECTOR_MAIN_FIGHT_FIGHTERS = "h3 a";
    static final String SELECTOR_EVENT_FIGHTS = ".event_match table tr";
    static final String SELECTOR_EVENT_DATE = ".authors_info .date meta[itemprop=\"startDate\"]";
    static final String SELECTOR_FIGHTER_URL = "abs:href";
    static final String SELECTOR_FIGHTER_NAME = "span[itemprop=\"name\"]";
    static final String SELECTOR_FIGHTER_DETAILS = "a[itemprop=\"url\"]";
    static final SimpleDateFormat MINUTES_SECONDS_DATE_FORMAT = new SimpleDateFormat("mm:ss");
    // Event fight table column indices
    static final int FIGHTER1_COLUMN = 1;
    static final int FIGHTER2_COLUMN = 3;
    static final int METHOD_COLUMN = 4;
    static final int ROUND_COLUMN = 5;
    static final int TIME_COLUMN = 6;
    // Messages
    static final String MSG_ERROR_CANNOT_PARSE_PROMOTION = "Cannot parse promotion for the event";
    static final String MSG_ERROR_CANNOT_PARSE_DATE = "Couldn't parse date";
    static final String MSG_ERROR_WRONG_FIGHTER_NUMBER =
            "Wrong fighter number, should be 1 or 2 (for first and second respectively)";
    // Private constructor for helper class
    private EventParserConstants() {
    }

}
