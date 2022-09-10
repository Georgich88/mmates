package com.georgeisaev.mmates.common.parser.config;

import lombok.experimental.UtilityClass;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import static java.time.format.DateTimeFormatter.ofPattern;

@UtilityClass
public class DateTimeConstants {

  public static final DateTimeFormatter DATE_FORMAT_YYYY_MM_DD = ofPattern("yyyy-MM-dd");
  public static final DateTimeFormatter DATE_FORMATTER_MMM_DD_YYYY_1 =
      ofPattern("MMM / dd / yyyy", Locale.US);
  public static final DateTimeFormatter DATE_FORMATTER_MMM_DD_YYYY_2 =
      ofPattern("MMM d, yyyy", Locale.US);

  public static final  DateTimeFormatter DATE_TIME_FORMATTER_MM_SS = new DateTimeFormatterBuilder()
      .appendPattern("mm:ss")
      .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
      .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
      .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
      .toFormatter();

  public static final  DateTimeFormatter DATE_TIME_FORMATTER_M_SS = new DateTimeFormatterBuilder()
      .appendPattern("m:ss")
      .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
      .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
      .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
      .toFormatter();

  public static Collection<DateTimeFormatter> dateTimeFormatters() {
    return List.of(
        DATE_FORMAT_YYYY_MM_DD, DATE_FORMATTER_MMM_DD_YYYY_1, DATE_FORMATTER_MMM_DD_YYYY_2);
  }

  public static Collection<DateTimeFormatter> minutesSecondsFormatters() {
    return List.of(DATE_TIME_FORMATTER_MM_SS, DATE_TIME_FORMATTER_M_SS);
  }
}
