package com.mmates.parsers.sherdog;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import com.mmates.core.model.events.Event;
import com.mmates.core.model.fights.Fight;
import com.mmates.core.model.fights.FightResult;
import com.mmates.core.model.people.Fighter;
import com.mmates.parsers.common.Parser;
import com.mmates.parsers.common.utils.Constants;
import com.mmates.parsers.common.utils.ParserUtils;
import com.mmates.parsers.common.utils.PictureProcessor;
import org.apache.commons.codec.digest.DigestUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mmates.sherdogparser.Constants;
import com.mmates.sherdogparser.PictureProcessor;
import com.mmates.sherdogparser.models.Event;
import com.mmates.sherdogparser.models.Fight;
import com.mmates.sherdogparser.models.FightResult;
import com.mmates.sherdogparser.models.FightType;
import com.mmates.sherdogparser.models.Fighter;

public class FighterParser implements Parser<Fighter> {

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
		this.PROCESSOR = Constants.DEFAULT_PICTURE_PROCESSOR;
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
		fighter.setSherdogUrl(ParserUtils.getSherdogPageUrl(doc));

		logger.info("Refreshing fighter {}", fighter.getSherdogUrl());

		try {
			Elements name = doc.select(".bio_fighter h1 span.fn");
			fighter.setName(name.get(0).html());
		} catch (Exception e) {
			// no info, skipping
		}

		// Getting nick name
		try {
			Elements nickname = doc.select(".bio_fighter span.nickname em");
			fighter.setNickname(nickname.get(0).html());
		} catch (Exception e) {
			// no info, skipping
		}

		// Birthday
		try {
			Elements birthday = doc.select("span[itemprop=\"birthDate\"]");
			fighter.setBirthday(dataFormant.parse(birthday.get(0).html()));
		} catch (Exception e) {
			// no info, skipping
		}
		// height
		try {
			Elements height = doc.select(".size_info .height strong");
			fighter.setHeight(height.get(0).html());
		} catch (Exception e) {
			// no info, skipping
		}
		// weight
		try {
			Elements weight = doc.select(".size_info .weight strong");
			fighter.setWeight(weight.get(0).html());
		} catch (Exception e) {
			// no info, skipping
		}
		// wins
		try {
			Elements wins = doc.select(".bio_graph .counter");
			fighter.setWins(Integer.parseInt(wins.get(0).html()));
		} catch (Exception e) {
			// no info, skipping
		}
		Elements winsMethods = doc.select(".bio_graph:first-of-type .graph_tag");
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
			Elements losses = doc.select(".bio_graph.loser .counter");
			fighter.setLosses(Integer.parseInt(losses.get(0).html()));
		} catch (Exception e) {
			// no info, skipping
		}

		Elements lossesMethods = doc.select(".bio_graph.loser .graph_tag");

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
				fight.setResult(getFightResult(tds.get(COLUMN_RESULT)));
				fight.setFighter2(getOpponent(tds.get(COLUMN_OPPONENT)));
				fight.setEvent(getEvent(tds.get(COLUMN_EVENT)));
				fight.setDate(getDate(tds.get(COLUMN_EVENT)));
				fight.setWinMethod(getWinMethod(tds.get(COLUMN_METHOD)));
				fight.setWinRound(getWinRound(tds.get(COLUMN_ROUND)));
				fight.setWinTime(getWinTime(tds.get(COLUMN_TIME)));
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
	private FightResult getFightResult(Element td) {
		return ParserUtils.getFightResult(td);
	}

	/**
	 * Get the fight result
	 *
	 * @param td a td from sherdogs table
	 * @return a fight result enum
	 */
	private Fighter getOpponent(Element td) {
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
	private Event getEvent(Element td) {
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
	private ZonedDateTime getDate(Element td) {
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
	private String getWinMethod(Element td) {
		return td.html().replaceAll("<br>(.*)", "");
	}

	/**
	 * Get the winning round
	 *
	 * @param td a td from sherdogs table
	 * @return an itneger
	 */
	private int getWinRound(Element td) {
		return Integer.parseInt(td.html());
	}

	/**
	 * Get time of win
	 *
	 * @param td a td from sherdogs table
	 * @return the time of win
	 */
	private String getWinTime(Element td) {
		return td.html();
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
