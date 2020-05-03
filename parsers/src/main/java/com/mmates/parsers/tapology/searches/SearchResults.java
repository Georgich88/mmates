package com.mmates.parsers.tapology.searches;

import com.mmates.core.model.Loadable;
import com.mmates.core.model.events.Event;
import com.mmates.core.model.people.Fighter;
import com.mmates.parsers.common.exceptions.ParserException;
import com.mmates.parsers.common.utils.Constants;
import com.mmates.parsers.tapology.Tapology;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SearchResults {

    private final String term;
    private final SearchWeightClass weightClass;
    private final String SEARCH_URL = "http://www.sherdog.com/stats/fightfinder?SearchTxt=%s&weight=%s&page=%d";
    private Tapology sherdog;
    private int page = 1;
    private List<Fighter> dryFighters = new ArrayList<>();
    private List<Event> dryEvents = new ArrayList<>();


    public SearchResults(String term, SearchWeightClass weightClass, Tapology sherdog) throws IOException {

        this.term = term;
        this.weightClass = weightClass;
        this.sherdog = sherdog;
        this.search();

    }


    /**
     * Triggers the actual search
     *
     * @throws IOException if anything goes wrong
     */
    private void search() throws IOException {

        String url = String.format(SEARCH_URL,
                term,
                (weightClass != null) ? weightClass.getValue() : "",
                page
        );

        dryEvents = new ArrayList<>();
        dryFighters = new ArrayList<>();

        List<Loadable> parse = new SearchParser().parse(url);

        parse.forEach(r -> {
            if (r.getSherdogUrl().startsWith(Constants.BASE_URL + "/events/")) {
                dryEvents.add((Event) r); // TODO: delete casting
            } else if (r.getSherdogUrl().startsWith(Constants.BASE_URL + "/fighter/")) {
                dryFighters.add((Fighter) r); // TODO: delete casting
            }
        });
    }


    /**
     * Gets the next result page
     *
     * @return itself
     * @throws IOException if the search http query fails
     */
    public SearchResults nextPage() throws IOException {
        this.page++;

        this.search();

        return this;
    }


    /**
     * Gets the simple version of the fighters (name and url only)
     *
     * @return the list of fighters
     */
    public List<Fighter> getFighters() {
        return dryFighters;
    }


    /**
     * Gets the simple version of the events(name and url only)
     *
     * @return the list of fighters
     */
    public List<Event> getEvents() {
        return dryEvents;
    }


    /**
     * Gets the fighter with the full data
     * This method can be long as it will query each fighter page
     *
     * @return the list of fighters
     */
    public List<Fighter> getFightersWithCompleteData() {

        return dryFighters.stream()
                .map(f -> {
                    try {
                        return sherdog.getFighter(f.getSherdogUrl());
                    } catch (IOException | ParseException e) {
                        return null; // TODO: delete null return
                    } catch (ParserException e) {
                        return null; // TODO: delete null return
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Gets the events with the full data
     * This method can be long as it will query each event page
     *
     * @return the list of events
     */
    public List<Event> getEventsWithCompleteData() {

        return dryEvents.stream()
                .map(f -> {
                    try {
                        return sherdog.getEvent(f.getSherdogUrl());
                    } catch (IOException | ParseException | ParserException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

    }
}



