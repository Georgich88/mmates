package com.mmates.parsers.tapology.utils;

import com.mmates.core.model.fights.Fight;
import com.mmates.core.model.fights.FightResult;
import com.mmates.core.model.fights.FightType;
import com.mmates.core.model.people.Fighter;
import com.mmates.parsers.tapology.Tapology;
import org.jsoup.nodes.Document;

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
                        .filter(f -> f.getFighter2() != null)
                        .filter(f -> f.getFighter2().getSherdogUrl().equalsIgnoreCase(f2.getSherdogUrl())
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

        Optional<Fighter> fighter = Optional.of(fight).map(Fight::getFighter1)
                .filter(f -> f.getSherdogUrl() != null && f.getSherdogUrl().length() > 0);

        if (fighter.isPresent()) {
            return getType.apply(fighter.get(), fight.getFighter2());
        } else {
            return getType.apply(fight.getFighter2(), fight.getFighter1());
        }

    }

    /**
     * Gets the url of a page using the meta tags in head
     *
     * @param doc the jsoup document to extract the page url from
     * @return the url of the document
     */
    public static String getSherdogPageUrl(Document doc) {
        String url = Optional.ofNullable(doc.head()).map(h -> h.select("meta")).map(
                es -> es.stream().filter(e -> e.attr("property").equalsIgnoreCase("og:url")).findFirst().orElse(null))
                .map(m -> m.attr("content")).orElse("");

        if (url.startsWith("//")) { // 2018-10-10 bug in sherdog where ig:url starts with //?
            url = url.replaceFirst("//", "http://");
        }

        return url.replace("http://", "https://");
    }
}
