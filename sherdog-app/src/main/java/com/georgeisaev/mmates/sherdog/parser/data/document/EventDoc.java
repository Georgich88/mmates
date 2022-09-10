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
@Document(collection = "mmates_sherdog_event")
public class EventDoc {

  @Id String id;
  String sherdogUrl;
  String name;
  PromotionDoc promotion;
  String ownership;
  LocalDate date;
  List<FightDoc> fights;
  String location;
  String venue;
  String enclosure;
}
