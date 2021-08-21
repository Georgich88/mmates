package com.mmates.core.dto.fights;

import com.mmates.core.dto.people.Fighter;
import com.mmates.core.dto.sources.SourceInformation;
import com.mmates.core.model.events.Event;
import io.swagger.annotations.ApiModel;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ApiModel(description = "Fight details")
public class Fight {

	UUID id;

	String name;

	Event event;

	Fighter firstFighter;

	Fighter secondFighter;

	LocalDateTime date;

	FightResult result;

	WinMethod winMethod;

	int winTime;

	int winRound;

	FightType type;

	Map<SourceInformation, String> links;


}
