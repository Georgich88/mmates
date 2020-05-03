package com.mmates.parsers.common;

import com.mmates.parsers.common.exceptions.NotParserSourceURLException;
import com.mmates.parsers.common.exceptions.ParserException;
import com.mmates.parsers.common.utils.Constants;
import com.mmates.parsers.common.utils.ParserUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.text.ParseException;

public interface Parser<T> {

    /**
     * Parse a page
     *
     * @param url of the site page
     * @return the object parsed by the parser
     * @throws IOException            if connecting to Sherdog fails
     * @throws ParseException         if the page structure has changed
     * @throws ParserException if anything related to the parser goes wrong
     */
    default T parse(String url) throws IOException, ParseException, ParserException {
        if (!url.startsWith("https")) {
            url = url.replace("http", "https");
        }
        if (!url.startsWith(Constants.BASE_HTTPS_URL)) {
            throw new NotParserSourceURLException();
        }

        Document doc = ParserUtils.parseDocument(url);
        return parseDocument(doc);
    }

    /**
     * Parses a document from the HTML source code directly
     *
     * @param html the HTML source code
     * @return the parsed object
     * @throws IOException    if connecting to Sherdog fails
     * @throws ParseException if the page structure has changed
     */
    default T parseFromHtml(String html) throws IOException, ParseException {
        return parseDocument(Jsoup.parse(html));
    }

    /**
     * Parses a jsoup document
     *
     * @param doc the document to parse
     * @return the parsed object
     * @throws IOException    if connecting to Sherdog fails
     * @throws ParseException if the page structure has changed
     */
    T parseDocument(Document doc) throws ParseException, IOException;


}
