package com.georgeisaev.mmates.sherdog.parser.data.document;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "mmates_sherdog_fighter")
public class FighterDoc {

  @Id String id;
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
