package com.mmates.core.dto.people;


import com.mmates.core.dto.fights.Fight;
import com.mmates.core.dto.sources.SourceInformation;
import com.mmates.core.dto.teams.Team;
import io.swagger.annotations.ApiModel;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ApiModel(description = "Fighter details")
public class Fighter {

	UUID id;
	String name;
	String nickname = "";
	int height = 0;
	int reach = 0;
	int legReach = 0;
	int weight = 0;
	LocalDate birthday;
	LocalDate debut;
	Team team;
	int wins = 0;
	int winsKo = 0;
	int winsSub = 0;
	int winsDec = 0;
	int winsOther = 0;
	int losses = 0;
	int lossesKo = 0;
	int lossesSub = 0;
	int lossesDec = 0;
	int lossesOther = 0;
	int draws = 0;
	int nc = 0;
	String picture;
	Map<SourceInformation, String> links;
	List<Fight> fights;
	List<Record> records;

}
