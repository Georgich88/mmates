package com.georgeisaev.mmates.sherdog.parser.data.parser.fighter;

import com.georgeisaev.mmates.common.parser.command.JsopAttributeParserCommand;
import com.georgeisaev.mmates.sherdog.domain.Fighter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.Collection;
import java.util.List;

import static com.georgeisaev.mmates.sherdog.parser.common.SherdogParserConstants.BASE_HTTPS_URL;

@Slf4j
@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum FighterAttributeParserCommand
    implements JsopAttributeParserCommand<Fighter.FighterBuilder> {

  // name
  NAME("name", ".fighter-info h1[itemprop=\"name\"] .fn"),
  NICKNAME("nickname", ".fighter-info h1[itemprop=\"name\"] .nickname em"),
  // birthDate
  BIRTH_DATE("birthDate", ".fighter-data span[itemprop=\"birthDate\"]"),
  // address
  ADDRESS_LOCALITY("addressLocality", ".adr .locality"),
  NATIONALITY("addressLocality", "[itemprop=\"nationality\"]"),
  // height
  HEIGHT_FT(
      "heightFt",
      ".fighter-data .bio-holder tr:nth-child(2) td:nth-child(2) b[itemprop=\"height\"]"),
  HEIGHT_CM("heightCm", ".fighter-data .bio-holder tr:nth-child(2) td:nth-child(2)"),
  // weight
  WEIGHT_KG(
      "weightKg",
      ".fighter-data .bio-holder tr:nth-child(2) td:nth-child(2) b[itemprop=\"height\"]"),
  WEIGHT_LBS("weightLbs", ".fighter-data .bio-holder tr:nth-child(2) td:nth-child(2)"),
  // picture
  PICTURE_URL("pictureUrl", ".module.bio_fighter.vcard .fighter-info img[itemprop=\"image\"]") {
    @Override
    public void parse(Document doc, Fighter.FighterBuilder builder) {
      final Elements picture = doc.select(getSelector());
      final String pictureUrl = BASE_HTTPS_URL + picture.attr("src").trim();
      builder.pictureUrl(pictureUrl);
    }
  };

  /** Attribute name */
  String attribute;

  /** CSS-like element selector, that finds elements matching a query */
  String selector;

  public static Collection<JsopAttributeParserCommand<Fighter.FighterBuilder>> availableCommands() {
    return List.of(values());
  }
}
