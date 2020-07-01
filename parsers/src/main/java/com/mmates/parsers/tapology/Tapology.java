package com.mmates.parsers.tapology;

import com.mmates.core.model.events.Event;
import com.mmates.core.model.people.Fighter;
import com.mmates.core.model.promotion.Promotion;
import com.mmates.parsers.common.exceptions.ParserException;
import com.mmates.parsers.common.utils.Constants;
import com.mmates.parsers.common.utils.PictureProcessor;
import com.mmates.parsers.tapology.events.EventParser;
import com.mmates.parsers.tapology.people.FighterParser;
import com.mmates.parsers.tapology.promotions.PromotionParser;
import com.mmates.parsers.tapology.searches.Search;

import java.io.IOException;
import java.text.ParseException;
import java.time.ZoneId;

public class Tapology {

	private ZoneId zoneId = ZoneId.systemDefault();
	private PictureProcessor pictureProcessor = Constants.DEFAULT_PICTURE_PROCESSOR;

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
	 * Gets an organization via it's Tapology URL.
	 *
	 * @param tapologyUrl Tapology URL, can find predefined url in Promotions.* enum.
	 * @return an Promotion
	 * @throws IOException            if connecting to tapology fails
	 * @throws ParseException         if the page structure has changed
	 * @throws ParserException if anythign related to the parser goes wrong
	 */
	public Promotion getPromotion(String tapologyUrl) throws IOException, ParseException, ParserException {
		return new PromotionParser(zoneId).parse(tapologyUrl);
	}

	/**
	 * Gets an organization via it's tapology page HTML, in case you want to have
	 * your own way of getting teh HTML content
	 *
	 * @param html The web page HTML
	 * @return an Promotion
	 * @throws IOException            if connecting to tapology fails
	 * @throws ParseException         if the page structure has changed
	 * @throws ParserException if anythign related to the parser goes wrong
	 */
	public Promotion getPromotionFromHtml(String html) throws IOException, ParseException, ParserException {
		return new PromotionParser(zoneId).parseFromHtml(html);
	}

	/**
	 * Gets an organization via it's tapology URL.
	 *
	 * @param promotion A promotion from the Promotions. enum
	 * @return an Promotion
	 * @throws IOException            if connecting to tapology fails
	 * @throws ParseException         if the page structure has changed
	 * @throws ParserException if anythign related to the parser goes wrong
	 */
	public Promotion getPromotion(Promotion promotion) throws IOException, ParseException, ParserException {
		return new PromotionParser(zoneId).parse(promotion.getTapologyUrl());
	}

	/**
	 * Gets an event via it's shergog page HTML
	 *
	 * @param html The web page HTML
	 * @return an Event
	 * @throws IOException            if connecting to tapology fails
	 * @throws ParseException         if the page structure has changed
	 * @throws ParserException if anythign related to the parser goes wrong
	 */
	public Event getEventFromHtml(String html) throws IOException, ParseException, ParserException {
		return new EventParser(zoneId).parseFromHtml(html);
	}

	/**
	 * Gets an event via it's tapology URL.
	 *
	 * @param tapologyUrl Tapology URL, can be found in the list of event of an
	 *                   organization
	 * @return an Event
	 * @throws IOException            if connecting to tapology fails
	 * @throws ParseException         if the page structure has changed
	 * @throws ParserException if anythign related to the parser goes wrong
	 */
	public Event getEvent(String tapologyUrl) throws IOException, ParseException, ParserException {
		EventParser eventParser = new EventParser(zoneId);
		eventParser.setFastMode(this.isFastMode());
		return eventParser.parse(tapologyUrl);
	}

	/**
	 * Get a fighter via it;s tapology page HTML
	 *
	 * @param html The web page HTML
	 * @return a Fighter an all his fights
	 * @throws IOException            if connecting to tapology fails
	 * @throws ParseException         if the page structure has changed
	 * @throws ParserException if anythign related to the parser goes wrong
	 */
	public Fighter getFighterFromHtml(String html) throws IOException, ParseException, ParserException {
		return new FighterParser(pictureProcessor, zoneId).parseFromHtml(html);
	}

	/**
	 * Get a fighter via it;s tapology URL.
	 *
	 * @param tapologyUrl the shergod url of the fighter
	 * @return a Fighter an all his fights
	 * @throws IOException            if connecting to tapology fails
	 * @throws ParseException         if the page structure has changed
	 * @throws ParserException if anythign related to the parser goes wrong
	 */
	public Fighter getFighter(String tapologyUrl) throws IOException, ParserException, ParseException {
		return new FighterParser(pictureProcessor, zoneId).parse(tapologyUrl);
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

		private Tapology parser = new Tapology();

		/**
		 * Sets a cache folder for the parser
		 *
		 * @param processor the picture processor to user with the parser check
		 *                  {@link PictureProcessor} for more info
		 * @return the tapology current state
		 */
		public Builder withPictureProcessor(PictureProcessor processor) {
			parser.setPictureProcessor(processor);
			return this;
		}

		/**
		 * Sets a timezone for the parser , this will help convert the timezone to the
		 * wanted timezone
		 *
		 * @param timezone timezone for the tapology builder
		 * @return the tapology current state
		 */
		public Builder withTimezone(String timezone) {
			parser.setZoneId(ZoneId.of(timezone));
			return this;
		}

		public Tapology build() {
			return parser;
		}

	}
}
