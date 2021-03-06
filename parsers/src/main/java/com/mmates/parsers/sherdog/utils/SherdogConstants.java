package com.mmates.parsers.sherdog.utils;

import com.mmates.parsers.common.utils.PictureProcessor;

public class SherdogConstants {

	public final static String SHERDOG_TIME_ZONE = "America/New_York";
	public final static int PARSING_TIMEOUT = 60000;
	public static final PictureProcessor DEFAULT_PICTURE_PROCESSOR = (u, f) -> u;
	public static final String BASE_URL = "http://www.sherdog.com";
	public static final String BASE_HTTPS_URL = "https://www.sherdog.com";
	public static final String BASE_ORGANIZATIONS_URL = "https://www.sherdog.com/organizations/";

	public static final String SHERDOG_RECENT_EVENT_URL = "https://www.sherdog.com/events/recent/%d-page/";
	public static final String SHERDOG_RECENT_EVENT_URL_TEMPLATE = "https://www.sherdog.com/events/recent/%d-page/";
	public static final String MSG_INFO_GETTING_RECENT_EVENTS = "Getting recent events {} ";
	public static final String SELECTOR_RECENT_EVENTS_TABLE = "#recentfights_tab .event tr";
	public static final String SELECTOR_TABLE_DATA = "td";
}
