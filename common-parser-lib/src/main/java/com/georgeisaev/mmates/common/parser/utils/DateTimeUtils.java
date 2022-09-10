package com.georgeisaev.mmates.common.parser.utils;

import com.georgeisaev.mmates.common.parser.config.DateTimeConstants;
import com.georgeisaev.mmates.common.parser.exception.ParserException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.time.LocalDate;
import java.time.LocalTime;

@Slf4j
@UtilityClass
public class DateTimeUtils {

  public static LocalDate parseDate(final String date) {
    for (val formatter : DateTimeConstants.dateTimeFormatters()) {
      try {
        return LocalDate.parse(date, formatter);
      } catch (Exception e) {
        log.trace("Cannot parse {} using {}", date, formatter, e);
      }
    }
    throw new ParserException("Cannot parse date=" + date);
  }

  public static int parseMinutesSeconds(final String minutesSeconds) {
    for (val formatter : DateTimeConstants.minutesSecondsFormatters()) {
      try {
        final LocalTime time = LocalTime.parse(minutesSeconds, formatter);
        return time.getMinute() * 60 + time.getSecond();
      } catch (Exception e) {
        log.trace("Cannot parse {} using {}", minutesSeconds, formatter, e);
      }
    }
    throw new ParserException("Cannot parse minutesSeconds=" + minutesSeconds);
  }
}
