package com.mmates.parsers.common.utils;

import com.mmates.core.model.fights.FightResult;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ParserUtils {

    /**
     * Gets the result of a fight following sherdog website win/lose/draw/nc Make
     * sure to use on Fighter1 only
     *
     * @param element Jsoup element
     * @return a FightResult
     */
    public static FightResult parseFightResult(Element element) {
        if (!element.select(".win").isEmpty()) {
            return FightResult.FIGHTER_1_WIN;
        } else if (!element.select(".loss").isEmpty()) {
            return FightResult.FIGHTER_2_WIN;
        } else if (!element.select(".draw").isEmpty()) {
            return FightResult.DRAW;
        } else if (!element.select(".no_contest").isEmpty()) {
            return FightResult.NO_CONTEST;
        } else {
            return FightResult.NOT_HAPPENED;
        }
    }

    /**
     * Parses a URL with all the required parameters
     *
     * @param url of the document to parse
     * @return the jsoup document
     * @throws IOException if the connection fails
     */
    public static Document parseDocument(String url) throws IOException {
        return Jsoup.connect(url).timeout(Constants.PARSING_TIMEOUT)
                .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                .referrer("http://www.google.com").get();
    }

    /**
     * Converts a String to the given timezone.
     *
     * @param date   Date to format
     * @param zoneId Zone id to convert from sherdog's time
     * @return the converted zonedatetime
     */
    public static ZonedDateTime convertStringToZonedDate(String date, ZoneId zoneId) throws DateTimeParseException {
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
    public static ZonedDateTime convertStringToZonedDate(String date, ZoneId zoneId, DateTimeFormatter formatter)
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

    /**
     * Downloads an image to a file with the adequate headers to the http query
     *
     * @param url  the url of the image
     * @param file the file to create
     * @throws IOException if the file download fails
     */
    public static void downloadImageToFile(String url, Path file) throws IOException {

        if (Files.exists(file.getParent())) {
            URL urlObject = new URL(url);
            URLConnection connection = urlObject.openConnection();
            connection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6");
            connection.setRequestProperty("Referer", "http://www.google.com");

            FileUtils.copyInputStreamToFile(connection.getInputStream(), file.toFile());
        }
    }

}
