package com.mmates.core.model.sources;

public enum SourceInformation {

    SHERDOG, TAPOLOGY, UFC, TWITTER, FACEBOOK, WIKIPEDIA, UFC_STATS, BEST_FIGHT_ODDS;

    public static SourceInformation defineSourceByUrl(String url) {
        if (url.contains("sherdog.com")) {
            return SHERDOG;
        } else if (url.contains("tapology.com")) {
            return TAPOLOGY;
        } else if (url.contains("ufc.com")) {
            return UFC;
        } else if (url.contains("twitter")) {
            return TWITTER;
        } else if (url.contains("facebook")) {
            return FACEBOOK;
        } else if (url.contains("wikipedia")) {
            return WIKIPEDIA;
        } else if (url.contains("ufcstats")) {
            return UFC_STATS;
        } else if (url.contains("bestfightodds.com")) {
            return BEST_FIGHT_ODDS;
        } else return null;
    }
}
