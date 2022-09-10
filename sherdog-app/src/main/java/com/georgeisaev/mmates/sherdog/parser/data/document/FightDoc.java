package com.georgeisaev.mmates.sherdog.parser.data.document;

import com.georgeisaev.mmates.sherdog.domain.FightResult;
import com.georgeisaev.mmates.sherdog.domain.FightType;
import com.georgeisaev.mmates.sherdog.domain.WinMethod;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FightDoc {

  String eventId;
  String firstFighterId;
  String secondFighterId;
  LocalDate date;
  FightResult result;
  WinMethod winMethod;
  Integer winTime;
  Integer winRound;
  FightType type;
}
