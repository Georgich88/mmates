package com.mmates.parsers.sherdog.people;

import com.mmates.core.model.events.Event;
import com.mmates.core.model.fights.Fight;
import com.mmates.core.model.fights.FightResult;
import com.mmates.core.model.fights.FightType;
import com.mmates.core.model.fights.WinMethod;
import com.mmates.core.model.people.Fighter;
import com.mmates.parsers.common.Parser;
import com.mmates.parsers.common.utils.ParserUtils;
import com.mmates.parsers.common.utils.PictureProcessor;
import com.mmates.parsers.sherdog.utils.SherdogConstants;
import com.mmates.parsers.sherdog.utils.SherdogParserUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class FighterParser implements Parser<Fighter> {

	public static final String MESSAGE_INFO_TEMPLATE_REFRESH_FIGHTER = "Refreshing fighter {}";
	// Selectors
	public static final String SELECTOR_FIGHTER_NAME_ELEMENT = ".bio_fighter h1 span.fn";
	public static final String SELECTOR_FIGHTER_NICKNAME_ELEMENT = ".bio_fighter span.nickname em";
	public static final String SELECTOR_FIGHTER_BIRTH_DATE_ELEMENT = "span[itemprop=\"birthDate\"]";
	public static final String SELECTOR_FIGHTER_HEIGHT_ELEMENT = ".size_info .height strong";
	public static final String SELECTOR_FIGHTER_WEIGHT_ELEMENT = ".size_info .weight strong";
	public static final String SELECTOR_FIGHTER_WINS_ELEMENT = ".bio_graph .counter";
	public static final String SELECTOR_FIGHTER_WINS_METHODS_ELEMENTS = ".bio_graph:first-of-type .graph_tag";
	public static final String SELECTOR_FIGHTER_LOSSES_ELEMENT = ".bio_graph.loser .counter";
	public static final String SELECTOR_FIGHTER_LOSSES_METHODS_ELEMENT = ".bio_graph.loser .graph_tag";

	private final SimpleDateFormat dataFormant = new SimpleDateFormat("yyyy-dd-MM");
	private final PictureProcessor PROCESSOR;
	private final ZoneId ZONE_ID;

	private static final int COLUMN_RESULT = 0;
	private static final int COLUMN_OPPONENT = 1;
	private static final int COLUMN_EVENT = 2;
	private static final int COLUMN_METHOD = 3;
	private static final int COLUMN_ROUND = 4;
	private static final int COLUMN_TIME = 5;
	private static final int METHOD_KO = 0;
	private static final int METHOD_SUBMISSION = 1;
	private static final int METHOD_DECISION = 2;
	private static final int METHOD_OTHERS = 3;

	private final Logger logger = LoggerFactory.getLogger(FighterParser.class);
	private boolean fastMode = false;

	public boolean isFastMode() {
		return fastMode;
	}

	public void setFastMode(boolean fastMode) {
		this.fastMode = fastMode;
	}

	/**
	 * Create a fight parser with a specified cache folder
	 *
	 * @param processor the picture processor to use for the fighter pictures
	 */
	public FighterParser(PictureProcessor processor) {
		this.PROCESSOR = processor;
		ZONE_ID = ZoneId.systemDefault();
	}

	/**
	 * Generates a fight parser with specified cache folder and zone id
	 *
	 * @param processor the picture processor to use for the fighter pictures
	 * @param zoneId    specified zone id for time conversion
	 */
	public FighterParser(PictureProcessor processor, ZoneId zoneId) {
		this.PROCESSOR = processor;
		this.ZONE_ID = zoneId;
	}

	/**
	 * FighterPArser with default cache folder location
	 *
	 * @param zoneId specified zone id for time conversion
	 */
	public FighterParser(ZoneId zoneId) {
		this.PROCESSOR = SherdogConstants.DEFAULT_PICTURE_PROCESSOR;
		ZONE_ID = zoneId;

	}

	/**
	 * Parse a Sherdog page
	 *
	 * @param doc Jsoup document of the sherdog page
	 * @throws IOException if connecting to sherdog fails
	 */
	@Override
	public Fighter parseDocument(Document doc) throws IOException {
		Fighter fighter = new Fighter();
		fighter.setSherdogUrl(SherdogParserUtils.getSherdogPageUrl(doc));

		logger.info(MESSAGE_INFO_TEMPLATE_REFRESH_FIGHTER, fighter.getSherdogUrl());

		try {
			Elements name = doc.select(SELECTOR_FIGHTER_NAME_ELEMENT);
			fighter.setName(name.get(0).html());
		} catch (Exception e) {
			// no info, skipping
		}

		// Getting nick name
		try {
			Elements nickname = doc.select(SELECTOR_FIGHTER_NICKNAME_ELEMENT);
			fighter.setNickname(nickname.get(0).html());
		} catch (Exception e) {
			// no info, skipping
		}

		// Birthday
		try {
			Elements birthday = doc.select(SELECTOR_FIGHTER_BIRTH_DATE_ELEMENT);
			fighter.setBirthday(dataFormant.parse(birthday.get(0).html()));
		} catch (Exception e) {
			// no info, skipping
		}
		// height
		try {
			Elements height = doc.select(SELECTOR_FIGHTER_HEIGHT_ELEMENT);
			fighter.setHeight(Integer.parseInt(height.get(0).html()));
		} catch (Exception e) {
			// no info, skipping
		}
		// weight
		try {
			Elements weight = doc.select(SELECTOR_FIGHTER_WEIGHT_ELEMENT);
			fighter.setWeight(Integer.parseInt(weight.get(0).html()));
		} catch (Exception e) {
			// no info, skipping
		}
		// wins
		try {
			Elements wins = doc.select(SELECTOR_FIGHTER_WINS_ELEMENT);
			fighter.setWins(Integer.parseInt(wins.get(0).html()));
		} catch (Exception e) {
			// no info, skipping
		}
		Elements winsMethods = doc.select(SELECTOR_FIGHTER_WINS_METHODS_ELEMENTS);
		try {
			fighter.setWinsKo(Integer.parseInt(winsMethods.get(METHOD_KO).html().split(" ")[0]));
		} catch (Exception e) {
			// no info, skipping
		}

		try {
			fighter.setWinsSub(Integer.parseInt(winsMethods.get(METHOD_SUBMISSION).html().split(" ")[0]));
		} catch (Exception e) {
			// no info, skipping
		}

		try {
			fighter.setWinsDec(Integer.parseInt(winsMethods.get(METHOD_DECISION).html().split(" ")[0]));
		} catch (Exception e) {
			// no info, skipping
		}

		try {
			fighter.setWinsOther(Integer.parseInt(winsMethods.get(METHOD_OTHERS).html().split(" ")[0]));
		} catch (Exception e) {
			// no info, skipping
		}
		// loses
		try {
			Elements losses = doc.select(SELECTOR_FIGHTER_LOSSES_ELEMENT);
			fighter.setLosses(Integer.parseInt(losses.get(0).html()));
		} catch (Exception e) {
			// no info, skipping
		}

		Elements lossesMethods = doc.select(SELECTOR_FIGHTER_LOSSES_METHODS_ELEMENT);

		try {

			fighter.setLossesKo((Integer.parseInt(lossesMethods.get(METHOD_KO).html().split(" ")[0])));
		} catch (Exception e) {
			// no info, skipping
		}

		try {
			fighter.setLossesSub(Integer.parseInt(lossesMethods.get(METHOD_SUBMISSION).html().split(" ")[0]));
		} catch (Exception e) {
			// no info, skipping
		}

		try {
			fighter.setLossesDec(Integer.parseInt(lossesMethods.get(METHOD_DECISION).html().split(" ")[0]));
		} catch (Exception e) {
			// no info, skipping
		}

		try {
			fighter.setLossesOther(Integer.parseInt(lossesMethods.get(METHOD_OTHERS).html().split(" ")[0]));
		} catch (Exception e) {
			// no info, skipping
		}
		// draws and NC
		Elements drawsNc = doc.select(".right_side .bio_graph .card");
		for (Element element : drawsNc) {

			switch (element.select("span.result").html()) {
				case "Draws":
					fighter.setDraws(Integer.parseInt(element.select("span.counter").html()));
					break;

				case "N/C":
					fighter.setNc(Integer.parseInt(element.select("span.counter").html()));
					break;
			}

		}

		Elements picture = doc.select(".bio_fighter .content img[itemprop=\"image\"]");
		String pictureUrl = "https://www.sherdog.com" + picture.attr("src").trim();

		Elements fightTables = doc.select(".fight_history");
		logger.info("Found {} fight history tables", fightTables.size());

		fightTables.stream()
				// excluding upcoming fights
				.filter(div -> !div.select(".module_header h2").html().trim().contains("Upcoming"))
				.collect(Collectors.groupingBy(div -> {
					String categoryName = div.select(".module_header h2").html().trim()
							.replaceAll("(?i)FIGHT HISTORY - ", "").trim();

					return FightType.fromString(categoryName);
				})).forEach((key, div) -> div.stream().map(d -> d.select(".table table tr"))
				.filter(tdList -> tdList.size() > 0).findFirst().ifPresent(tdList -> {
					List<Fight> f = getFights(tdList, fighter);

					f.forEach(fight -> fight.setType(key));

					fighter.getFights().addAll(f);
				}));

		List<Fight> sorted = fighter.getFights().stream()
				.sorted(Comparator.comparing(Fight::getDate, Comparator.nullsFirst(Comparator.naturalOrder())))
				.collect(Collectors.toList());

		fighter.setFights(sorted);

		logger.info("Found {} fights for {}", fighter.getFights().size(), fighter.getName());

		// setting the picture last to make sure the fighter variable has all the data
		if (pictureUrl.length() > 0) {
			fighter.setPicture(PROCESSOR.process(pictureUrl, fighter));
		}

		return fighter;
	}

	/**
	 * Get a fighter fights
	 *
	 * @param trs     JSOUP TRs document
	 * @param fighter a fighter to parse against
	 */
	private List<Fight> getFights(Elements trs, Fighter fighter) throws ArrayIndexOutOfBoundsException {
		List<Fight> fights = new ArrayList<>();

		logger.info("{} TRs to parse through", trs.size());

		Fighter sFighter = new Fighter();
		sFighter.setName(fighter.getName());
		sFighter.setSherdogUrl(fighter.getSherdogUrl());

		// removing header row...
		if (trs.size() > 0) {
			trs.remove(0);

			trs.forEach(tr -> {
				Fight fight = new Fight();
				fight.setFighter1(sFighter);

				Elements tds = tr.select("td");
				fight.setResult(parseFightResult(tds.get(COLUMN_RESULT)));
				fight.setFighter2(parseOpponent(tds.get(COLUMN_OPPONENT)));
				fight.setEvent(parseEvent(tds.get(COLUMN_EVENT)));
				fight.setDate(parseDate(tds.get(COLUMN_EVENT)));
				fight.setWinMethod(WinMethod.defineWinMethod(parseWinMethod(tds.get(COLUMN_METHOD))));
				fight.setWinRound(parseWinRound(tds.get(COLUMN_ROUND)));
				fight.setWinTime(parseWinTime(tds.get(COLUMN_TIME)));
				fights.add(fight);
				logger.info("{}", fight);
			});
		}

		return fights;
	}

	/**
	 * Get the fight result
	 *
	 * @param td a td from sherdogs table
	 * @return a fight result enum
	 */
	private FightResult parseFightResult(Element td) {
		return ParserUtils.getFightResult(td);
	}

	/**
	 * Get the fight result
	 *
	 * @param td a td from sherdogs table
	 * @return a fight result enum
	 */
	private Fighter parseOpponent(Element td) {
		Fighter opponent = new Fighter();
		Element opponentLink = td.select("a").get(0);
		opponent.setName(opponentLink.html());
		opponent.setSherdogUrl(opponentLink.attr("abs:href"));

		return opponent;
	}

	/**
	 * Get the fight event
	 *
	 * @param td a td from sherdogs table
	 * @return a sherdog base object with url and name
	 */
	private Event parseEvent(Element td) {
		Element link = td.select("a").get(0);

		Event event = new Event();
		event.setName(link.html().replaceAll("<span itemprop=\"award\">|</span>", ""));
		event.setSherdogUrl(link.attr("abs:href"));

		return event;
	}

	/**
	 * Get the date of the fight
	 *
	 * @param td a td from sherdogs table
	 * @return the zonedatetime of the fight
	 */
	private ZonedDateTime parseDate(Element td) {
		// date
		Element date = td.select("span.sub_line").first();

		return ParserUtils.getDateFromStringToZoneId(date.html(), ZONE_ID,
				DateTimeFormatter.ofPattern("MMM / dd / yyyy", Locale.US));
	}

	/**
	 * Get the winning method
	 *
	 * @param td a td from sherdogs table
	 * @return a string with the finishing method
	 */
	private String parseWinMethod(Element td) {
		return td.html().replaceAll("<br>(.*)", "");
	}

	/**
	 * Get the winning round
	 *
	 * @param td a td from sherdogs table
	 * @return an itneger
	 */
	private int parseWinRound(Element td) {
		return Integer.parseInt(td.html());
	}

	/**
	 * Get time of win
	 *
	 * @param td a td from sherdogs table
	 * @return the time of win
	 */
	private int parseWinTime(Element td) {
		try {
			SimpleDateFormat minutesSecondsDateFormat = new SimpleDateFormat("mm:ss");
			Date date = minutesSecondsDateFormat.parse(td.html());
			return date.getSeconds();

		} catch (ParseException e) {
			return 0;
		}

	}

	/**
	 * Hashes a string
	 *
	 * @param s the string to hash
	 * @return the hashed string
	 */
	private String hash(String s) {
		return DigestUtils.sha256Hex(s);
	}
}
