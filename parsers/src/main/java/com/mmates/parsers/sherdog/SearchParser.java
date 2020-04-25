package com.mmates.parsers.sherdog;

import com.mmates.core.model.Loadable;
import com.mmates.parsers.common.utils.ParserUtils;
import com.mmates.sherdogparser.Constants;
import com.mmates.sherdogparser.models.SherdogBaseObject;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SearchParser {

	private final Logger logger = LoggerFactory.getLogger(EventParser.class);

	/**
	 * Parses a search page results
	 *
	 * @param url the search url
	 * @return the results
	 */
	public List<Loadable> parse(String url) throws IOException {

		Document document = ParserUtils.parseDocument(url);

		Elements select = document.select(".fightfinder_result tr");

		// Remove the first one as it's the header
		if (select.size() > 0) {
			select.remove(0);
		}

		return select.stream()
				.map(e -> Optional.ofNullable(e.select("td"))
				// second element is the fighter name
				.filter(t -> t.size() > 1)
				.map(td -> td.get(1))
				.map(t -> t.select("a"))
				.filter(t -> t.size() == 1)
				.map(t -> t.get(0))
				.map(t -> {
					// this could be either fighter or event
					Loadable sherdogObject = new SherdogBaseObject();
					sherdogObject.setName(t.text());
					sherdogObject.setSherdogUrl(Constants.BASE_URL + t.attr("href"));

					return sherdogObject;
				}).filter(f -> f.getName() != null && f.getSherdogUrl() != null).orElse(null))
				.filter(f -> f != null).collect(Collectors.toList());

	}

}
