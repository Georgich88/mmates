package com.mmates.parsers.sherdog.utils;

import com.mmates.core.model.fights.Fight;
import com.mmates.core.model.fights.FightType;
import com.mmates.core.model.people.Fighter;
import com.mmates.parsers.sherdog.Sherdog;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.function.BiFunction;

import static com.mmates.core.model.fights.FightResult.NOT_HAPPENED;
import static com.mmates.core.model.fights.FightType.PRO;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.slf4j.LoggerFactory.getLogger;

public class SherdogParserUtils {

    // Messages
    private static final String MSG_ERROR_CANNOT_RETRIEVE_FIGHT_TYPE = "Cannot retrieve a fight type from the fight";
    // Logger
    private static final Logger logger = getLogger(SherdogParserUtils.class);

    // Constructors
    private SherdogParserUtils() {
    }

    /**
     * Retrieve the type of a fight (Pro, amateur etc...) from the Sherdog site.
     * Assume that this is a pro-fight, if we fight type cannot be retrieved.
     *
     * @param parser the Sherdog parser, required as we need to use the parser to get info on the fighter
     * @param fight  the fight to check
     * @return the type of the fight
     */
    public static FightType retrieveFightType(Sherdog parser, Fight fight) {
        // getting one of the fighter, and parsing his/her fights to find this fight
        BiFunction<Fighter, Fighter, FightType> getType = (firstFighter, secondFighter) -> {
            try {
                Fighter fighter = parser.parseFighterFromUrl(firstFighter.getSherdogUrl());
                return fighter.getFights().stream()
                        .filter(f -> f.getResult() != NOT_HAPPENED)
                        .filter(f -> f.getSecondFighter() != null)
                        .filter(f -> f.getSecondFighter().getSherdogUrl().equalsIgnoreCase(secondFighter.getSherdogUrl())
                                && f.getEvent().getSherdogUrl().equalsIgnoreCase(fight.getEvent().getSherdogUrl()))
                        .findFirst().map(Fight::getType).orElse(PRO);
            } catch (Exception e) {
                logger.error(MSG_ERROR_CANNOT_RETRIEVE_FIGHT_TYPE, e);
                return PRO;
            }
        };
        if (fight.getResult() == NOT_HAPPENED) {
            return FightType.UPCOMING;
        }
        Optional<Fighter> fighter = of(fight)
                .map(Fight::getFirstFighter)
                .filter(f -> f.getSherdogUrl() != null
                        && f.getSherdogUrl().length() > 0);
        if (fighter.isPresent()) {
            return getType.apply(fighter.get(), fight.getSecondFighter());
        } else {
            return getType.apply(fight.getSecondFighter(), fight.getFirstFighter());
        }
    }

    /**
     * Retrieve an url of a page using the meta tags in head
     *
     * @param document the jsoup document to extract the page url from
     * @return the url of the document
     */
    public static String retrieveSherdogPageUrl(Document document) {
        String url = ofNullable(document.head())
                .map(h -> h.select("meta"))
                .flatMap(es -> es.stream()
                        .filter(e -> e.attr("property").equalsIgnoreCase("og:url"))
                        .findFirst())
                .map(m -> m.attr("content")).orElse("");
        if (url.startsWith("//")) { // 2018-10-10 bug in sherdog where ig:url starts with //?
            url = url.replaceFirst("//", "http://");
        }
        return url.replace("http://", "https://");
    }

    public static String parseEventUrl(Element td) {
        Elements url = td.select("a[itemprop=\"url\"]");
        return url.isEmpty() ? "" : url.get(0).attr("abs:href");
    }

}
