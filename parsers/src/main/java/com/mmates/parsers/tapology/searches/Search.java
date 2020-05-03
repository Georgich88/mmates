package com.mmates.parsers.tapology.searches;

import com.mmates.parsers.tapology.Tapology;

import java.io.IOException;

public class Search {

    private String term;
    private SearchWeightClass weightClass = null;

    private Tapology sherdog;

    public Search(String term, Tapology sherdog) {
        this.term = term;
        this.sherdog = sherdog;
    }

    /**
     * Filter the search results by weight class
     * @param weightClass the chosen weight class
     * @return
     */
    public Search withWeightClass(SearchWeightClass weightClass){

        this.weightClass = weightClass;
        return this;
    }


    /**
     * Performs the search
     * @return the search results
     * @throws IOException when the query fails
     */
    public SearchResults query() throws IOException {
        return new SearchResults(this.term, this.weightClass, sherdog);
    }

}
