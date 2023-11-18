package com.georgeisaev.mmates.sherdog.parser.data.document;

import static com.georgeisaev.mmates.sherdog.parser.data.document.FieldAlias.SHERDOG_URL;

import java.time.LocalDate;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "mmates-sherdog-fighter")
public class FighterDoc {

  @Id String id;

  @Field(name = SHERDOG_URL)
  String sherdogUrl;

  String pictureUrl;
  String name;
  String nickname;
  LocalDate birthDate;
  String addressLocality;
  String nationality;
  String heightFt;
  String heightCm;
  String weightLbs;
  String weightKg;
  String association;
  String weightClass;
  Long winsTotals;
  Long winsKoTko;
  Long winsSubmissions;
  Long winsDecisions;
  Integer winsOther;
  Long lossesTotals;
  Long lossesKoTko;
  Long lossesSubmissions;
  Long lossesDecisions;
  Integer lossesOther;
  Integer draws;
  Integer nc;
  List<FightDoc> fights;
}
