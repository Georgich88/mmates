package com.mmates.parsers.tapology.utils;

import com.mmates.core.model.fights.Fight;
import com.mmates.core.model.fights.FightResult;
import com.mmates.core.model.fights.FightType;
import com.mmates.core.model.people.Fighter;
import com.mmates.parsers.common.utils.Constants;
import com.mmates.parsers.tapology.Tapology;
import org.jsoup.nodes.Document;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.function.BiFunction;

public class TapologyParserUtils {

    /**
     * Gets the type of a fight (Pro, amateur etc...)
     *
     * @param parser the sherdsog parser, required as we need to use the parser to
     *               get info on the fighter
     * @param fight  the fight to check
     * @return the type of the fight
     */
    public static FightType getFightType(Tapology parser, Fight fight) {

        // getting one of the fighter, and parsing his/her fights to find this fight

        BiFunction<Fighter, Fighter, FightType> getType = (f1, f2) -> {
            try {
                Fighter fighter = parser.getFighter(f1.getSherdogUrl());
                return fighter.getFights().stream()
                        .filter(f -> f.getResult() != FightResult.NOT_HAPPENED)
                        .filter(f -> f.getSecondFighter() != null)
                        .filter(f -> f.getSecondFighter().getSherdogUrl().equalsIgnoreCase(f2.getSherdogUrl())
                                && f.getEvent().getSherdogUrl().equalsIgnoreCase(fight.getEvent().getSherdogUrl()))
                        .findFirst().map(Fight::getType).orElse(FightType.PRO); // if we don't know, assume pro
            } catch (Exception e) {
                e.printStackTrace();
                return FightType.PRO; // if we dont know assume pro
            }
        };

        if (fight.getResult() == FightResult.NOT_HAPPENED) {
            return FightType.UPCOMING;
        }

        Optional<Fighter> fighter = Optional.of(fight).map(Fight::getFirstFighter)
                .filter(f -> f.getSherdogUrl() != null && f.getSherdogUrl().length() > 0);

        if (fighter.isPresent()) {
            return getType.apply(fighter.get(), fight.getSecondFighter());
        } else {
            return getType.apply(fight.getSecondFighter(), fight.getFirstFighter());
        }

    }

    /**
     * Gets the url of a page using the meta tags in head
     *
     * @param doc the jsoup document to extract the page url from
     * @return the url of the document
     */
    public static String getTapologyPageUrl(Document doc) {
        String url = Optional.ofNullable(doc.head()).map(h -> h.select("meta")).map(
                es -> es.stream().filter(e -> e.attr("property").equalsIgnoreCase("og:url")).findFirst().orElse(null))
                .map(m -> m.attr("content")).orElse("");

        if (url.startsWith("//")) { // 2018-10-10 bug in sherdog where ig:url starts with //?
            url = url.replaceFirst("//", "http://");
        }

        return url.replace("http://", "https://");
    }


    /**
     * Converts a String to the given timezone.
     *
     * @param date   Date to format
     * @param zoneId Zone id to convert from sherdog's time
     * @return the converted zonedatetime
     */
    public static ZonedDateTime getDateFromStringToZoneId(String date, ZoneId zoneId) throws DateTimeParseException {
        ZonedDateTime usDate = ZonedDateTime.parse(date).withZoneSameInstant(ZoneId.of(Constants.SHERDOG_TIME_ZONE));
        return usDate.withZoneSameInstant(zoneId);
    }

    /**
     * Converts a String to the given timezone.
     *
     * @param date      Date to format
     * @param zoneId    Zone id to convert from sherdog's time
     * @param formatter Formatter for exotic date format
     * @return the converted zonedatetime
     */
    public static ZonedDateTime getDateFromStringToZoneId(String date, ZoneId zoneId, DateTimeFormatter formatter)
            throws DateTimeParseException {
        try {
            // noticed that date not parsed with non-US locale. For me this fix is helpful
            LocalDate localDate = LocalDate.parse(date, formatter);
            ZonedDateTime usDate = localDate.atStartOfDay(zoneId);
            return usDate.withZoneSameInstant(zoneId);
        } catch (Exception e) {
            // In case the parsing fail, we try without time
            try {
                ZonedDateTime usDate = LocalDate.parse(date, formatter)
                        .atStartOfDay(ZoneId.of(Constants.SHERDOG_TIME_ZONE));
                return usDate.withZoneSameInstant(zoneId);
            } catch (DateTimeParseException e2) {
                return null;
            }
        }
    }

}
