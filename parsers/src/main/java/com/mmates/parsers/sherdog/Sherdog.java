package com.mmates.parsers.sherdog;

import com.mmates.core.model.events.Event;
import com.mmates.core.model.people.Fighter;
import com.mmates.core.model.promotion.Promotion;
import com.mmates.parsers.common.exceptions.ParserException;
import com.mmates.parsers.common.utils.Constants;
import com.mmates.parsers.common.utils.PictureProcessor;
import com.mmates.parsers.sherdog.events.EventParser;
import com.mmates.parsers.sherdog.people.FighterParser;
import com.mmates.parsers.sherdog.promotions.PromotionParser;
import com.mmates.parsers.sherdog.searches.Search;

import java.io.IOException;
import java.text.ParseException;
import java.time.ZoneId;

public class Sherdog {

	private ZoneId zoneId = ZoneId.systemDefault();
	private PictureProcessor pictureProcessor = Constants.DEFAULT_PICTURE_PROCESSOR;
	public final static String BASE_URL = "https://www.sherdog.com/";

	public boolean fastMode = false;

	public boolean isFastMode() {
		return fastMode;
	}

	public void setFastMode(boolean fastMode) {
		this.fastMode = fastMode;
	}

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
	public ZoneId getZoneId() {
		return zoneId;
	}

	/**
	 * Sets the zoneod
	 *
	 * @param zoneId which zone id the event times need to be converted
	 */
	public void setZoneId(ZoneId zoneId) {
		this.zoneId = zoneId;
	}

	/**
	 * Gets an organization via it's Sherdog URL.
	 *
	 * @param sherdogUrl Sherdog URL, can find predefined url in Promotions.* enum.
	 * @return an Promotion
	 * @throws IOException            if connecting to sherdog fails
	 * @throws ParseException         if the page structure has changed
	 * @throws ParserException if anythign related to the parser goes wrong
	 */
	public Promotion getPromotion(String sherdogUrl) throws IOException, ParseException, ParserException {
		return new PromotionParser(zoneId).parse(sherdogUrl);
	}

	/**
	 * Gets an organization via it's sherdog page HTML, in case you want to have
	 * your own way of getting teh HTML content
	 *
	 * @param html The web page HTML
	 * @return an Promotion
	 * @throws IOException            if connecting to sherdog fails
	 * @throws ParseException         if the page structure has changed
	 * @throws ParserException if anythign related to the parser goes wrong
	 */
	public Promotion getPromotionFromHtml(String html) throws IOException, ParseException, ParserException {
		return new PromotionParser(zoneId).parseFromHtml(html);
	}

	/**
	 * Gets an organization via it's sherdog URL.
	 *
	 * @param promotion A promotion from the Promotions. enum
	 * @return an Promotion
	 * @throws IOException            if connecting to sherdog fails
	 * @throws ParseException         if the page structure has changed
	 * @throws ParserException if anythign related to the parser goes wrong
	 */
	public Promotion getPromotion(Promotion promotion) throws IOException, ParseException, ParserException {
		return new PromotionParser(zoneId).parse(promotion.getSherdogUrl());
	}

	/**
	 * Gets an event via it's shergog page HTML
	 *
	 * @param html The web page HTML
	 * @return an Event
	 * @throws IOException            if connecting to sherdog fails
	 * @throws ParseException         if the page structure has changed
	 * @throws ParserException if anythign related to the parser goes wrong
	 */
	public Event getEventFromHtml(String html) throws IOException, ParseException, ParserException {
		return new EventParser(zoneId).parseFromHtml(html);
	}

	/**
	 * Gets an event via it's sherdog URL.
	 *
	 * @param sherdogUrl Sherdog URL, can be found in the list of event of an
	 *                   organization
	 * @return an Event
	 * @throws IOException            if connecting to sherdog fails
	 * @throws ParseException         if the page structure has changed
	 * @throws ParserException if anythign related to the parser goes wrong
	 */
	public Event getEvent(String sherdogUrl) throws IOException, ParseException, ParserException {
		EventParser eventParser = new EventParser(zoneId);
		eventParser.setFastMode(this.isFastMode());
		return eventParser.parse(sherdogUrl);
	}

	/**
	 * Get a fighter via it;s sherdog page HTML
	 *
	 * @param html The web page HTML
	 * @return a Fighter an all his fights
	 * @throws IOException            if connecting to sherdog fails
	 * @throws ParseException         if the page structure has changed
	 * @throws ParserException if anythign related to the parser goes wrong
	 */
	public Fighter getFighterFromHtml(String html) throws IOException, ParseException, ParserException {
		return new FighterParser(pictureProcessor, zoneId).parseFromHtml(html);
	}

	/**
	 * Get a fighter via it;s sherdog URL.
	 *
	 * @param sherdogUrl the shergod url of the fighter
	 * @return a Fighter an all his fights
	 * @throws IOException            if connecting to sherdog fails
	 * @throws ParseException         if the page structure has changed
	 * @throws ParserException if anythign related to the parser goes wrong
	 */
	public Fighter getFighter(String sherdogUrl) throws IOException, ParserException, ParseException {
		return new FighterParser(pictureProcessor, zoneId).parse(sherdogUrl);
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

	/**
	 * Builder
	 */
	public static class Builder {

		private Sherdog parser = new Sherdog();

		/**
		 * Sets a cache folder for the parser
		 *
		 * @param processor the picture processor to user with the parser check
		 *                  {@link PictureProcessor} for more info
		 * @return the sherdog current state
		 */
		public Builder withPictureProcessor(PictureProcessor processor) {
			parser.setPictureProcessor(processor);
			return this;
		}

		/**
		 * Sets a timezone for the parser , this will help convert the timezone to the
		 * wanted timezone
		 *
		 * @param timezone timezone for the sherdog builder
		 * @return the sherdog current state
		 */
		public Builder withTimezone(String timezone) {
			parser.setZoneId(ZoneId.of(timezone));
			return this;
		}

		public Sherdog build() {
			return parser;
		}

	}
}
