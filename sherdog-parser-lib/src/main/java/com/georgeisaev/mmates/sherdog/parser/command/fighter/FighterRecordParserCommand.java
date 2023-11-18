package com.georgeisaev.mmates.sherdog.parser.command.fighter;

import com.georgeisaev.mmates.sherdog.domain.FighterRecord;
import com.georgeisaev.mmates.sherdog.domain.FighterRecordDetails;
import com.georgeisaev.mmates.sherdog.parser.command.Jsoup2SherdogParserCommand;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.Collection;
import java.util.List;
import java.util.function.IntConsumer;

import static com.georgeisaev.mmates.sherdog.parser.utils.SherdogParserUtils.MSG_ERR_CANNOT_PARSE_PROPERTY;
import static java.lang.Integer.parseInt;

@Slf4j
@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum FighterRecordParserCommand
    implements Jsoup2SherdogParserCommand<FighterRecord.FighterRecordBuilder> {

  // wins
  WINS_TOTAL("winsTotals", ".winloses.win span:nth-child(2)"),
  WINS("", ".fighter-data .winsloses-holder .wins .meter .pl") {
    @Override
    public void parse(final Document source, final FighterRecord.FighterRecordBuilder object) {
      object.winsDetails(parseDetails(source, FighterRecordDetails.builder()).build());
    }
  },
  // loses
  LOSES_TOTAL("lossesTotals", ".winloses.lose span:nth-child(2)"),
  LOSES("", ".fighter-data .winsloses-holder .loses .meter .pl") {
    @Override
    public void parse(final Document source, final FighterRecord.FighterRecordBuilder object) {
      object.lossesDetails(parseDetails(source, FighterRecordDetails.builder()).build());
    }
  },
  // draws and no contest
  DRAWS("draws", ".winloses.draws span:nth-child(2)"),
  NC("nc", ".winloses.nc span:nth-child(2)"),
  ;

  /** Attribute name */
  String attribute;

  /** CSS-like element selector, that finds elements matching a query */
  String selector;

  public static Collection<FighterRecordParserCommand> availableCommands() {
    return List.of(values());
  }

  protected FighterRecordDetails.FighterRecordDetailsBuilder parseDetails(
      @NotNull final Document source,
      @NotNull final FighterRecordDetails.FighterRecordDetailsBuilder target) {
    final Elements methods = source.select(getSelector());
    extractRecordByMethodProperty(methods, FighterRecordMethods.METHOD_KO, target::koTko);
    extractRecordByMethodProperty(
        methods, FighterRecordMethods.METHOD_SUBMISSION, target::submissions);
    extractRecordByMethodProperty(methods, FighterRecordMethods.METHOD_DECISION, target::decisions);
    try {
      extractRecordByMethodProperty(methods, FighterRecordMethods.METHOD_OTHERS, target::other);
    } catch (Exception e) {
      log.error(MSG_ERR_CANNOT_PARSE_PROPERTY, "other", source.baseUri(), e);
    }
    return target;
  }

  private static void extractRecordByMethodProperty(
      @NotNull final Elements methods, final int method, @NotNull final IntConsumer setter) {
    setter.accept(parseInt(methods.get(method).html().split(" ")[0]));
  }

  @UtilityClass
  private static class FighterRecordMethods {

    private static final int METHOD_KO = 0;
    private static final int METHOD_SUBMISSION = 1;
    private static final int METHOD_DECISION = 2;
    private static final int METHOD_OTHERS = 3;
  }
}
