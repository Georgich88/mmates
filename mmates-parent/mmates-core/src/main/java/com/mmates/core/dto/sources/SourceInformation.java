package com.mmates.core.dto.sources;

import io.swagger.annotations.ApiModel;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@ApiModel(description = "External sources of information")
public enum SourceInformation {

	SHERDOG("Sherdog"),
	TAPOLOGY("Tapology"),
	UFC("UFC"),
	TWITTER("Twitter"),
	FACEBOOK("Facebook"),
	WIKIPEDIA("Wikipedia"),
	UFC_STATS("UFC Stats"),
	BEST_FIGHT_ODDS(""),
	FIGHT_MATRIX("");

	String name;

	public static SourceInformation defineSourceByUrl(String url) {

		switch (url.toUpperCase().trim()) {
			case ("sherdog.com"): {
				return SHERDOG;
			}
			case ("tapology.com"): {
				return TAPOLOGY;
			}
			case ("ufc.com"): {
				return UFC;
			}
			case ("twitter"): {
				return TWITTER;
			}
			case ("facebook"): {
				return FACEBOOK;
			}
			case ("wikipedia"): {
				return WIKIPEDIA;
			}
			case ("ufcstats"): {
				return UFC_STATS;
			}
			case ("bestfightodds.com"): {
				return BEST_FIGHT_ODDS;
			}
			default: {
				return null;
			}
		}
	}

}
