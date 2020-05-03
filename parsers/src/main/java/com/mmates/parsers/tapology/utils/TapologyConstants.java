package com.mmates.parsers.tapology.utils;

import com.mmates.parsers.common.utils.PictureProcessor;

public class TapologyConstants {

	public final static String SHERDOG_TIME_ZONE = "America/New_York";
	public final static int PARSING_TIMEOUT = 60000;
	public static final PictureProcessor DEFAULT_PICTURE_PROCESSOR = (u, f) -> u;
	public static final String BASE_URL = "http://www.tapology.com";
	public static final String BASE_HTTPS_URL = "https://www.tapology.com";
	public static final String BASE_ORGANIZATIONS_URL = "https://www.tapology.com/organizations/";

	public static final String SHERDOG_RECENT_EVENT_URL = "https://www.tapology.com/events/recent/%d-page/";
}
