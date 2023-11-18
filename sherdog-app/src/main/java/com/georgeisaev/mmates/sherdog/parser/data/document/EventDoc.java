package com.georgeisaev.mmates.sherdog.parser.data.document;

import static com.georgeisaev.mmates.sherdog.parser.data.document.FieldAlias.*;

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
@Document(collection = "mmates-sherdog-event")
public class EventDoc {

  @Id String id;

  @Field(name = SHERDOG_URL)
  String sherdogUrl;

  @Field(name = NAME)
  String name;

  @Field(name = PROMOTION)
  PromotionDoc promotion;

  @Field(name = OWNERSHIP)
  String ownership;

  @Field(name = DATE)
  LocalDate date;

  @Field(name = FIGHTS)
  List<FightDoc> fights;

  @Field(name = LOCATION)
  String location;

  @Field(name = VENUE)
  String venue;

  @Field(name = ENCLOSURE)
  String enclosure;
}
