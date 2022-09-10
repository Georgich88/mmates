package com.georgeisaev.mmates.common.parser.utils;

import com.georgeisaev.mmates.common.parser.SherdogParserConstants;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.select.Selector;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
@UtilityClass
public class CommonParserUtils {

  public static final String MSG_ERR_CANNOT_PARSE_PROPERTY = "Cannot parse property {} from {}";

  /**
   * Parses a URL with all the required parameters
   *
   * @param url of the document to parse
   * @return the jsoup document
   * @throws IOException if the connection fails
   */
  public static Document parseDocument(String url) throws IOException {
    return Jsoup.connect(url)
        .timeout(SherdogParserConstants.PARSING_TIMEOUT)
        .userAgent(
            "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725"
                + " Firefox/2.0.0.6")
        .referrer("https://www.google.com")
        .get();
  }

  /**
   * Extracts field value and set it to the object
   *
   * @param doc an HTML Document.
   * @param selector a {@link Selector} CSS-like query
   * @param propertyName a field name
   * @param setter an object setter
   * @param extractor an extractor of a field value
   * @param <T> type of field
   */
  public static <T> void extractAndSet(
      Document doc,
      String selector,
      String propertyName,
      Consumer<T> setter,
      Function<Elements, T> extractor) {
    try {
      setter.accept(extractor.apply(doc.select(selector)));
    } catch (Exception e) {
      log.error(MSG_ERR_CANNOT_PARSE_PROPERTY, propertyName, doc.baseUri(), e);
    }
  }
}
