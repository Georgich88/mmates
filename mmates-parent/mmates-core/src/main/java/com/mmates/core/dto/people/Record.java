package com.mmates.core.dto.people;


import com.mmates.core.dto.fights.Fight;
import io.swagger.annotations.ApiModel;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ApiModel(description = "Professional record of the fighter")
public class Record {

	Fighter fighter;
	Fight fight;
	LocalDateTime date;

	int wins = 0;
	int winsKo = 0;
	int winsTko = 0;
	int winsSub = 0;
	int winsDec = 0;
	int winsOther = 0;
	int losses = 0;
	int lossesKo = 0;
	int lossesTko = 0;
	int lossesSub = 0;
	int lossesDec = 0;
	int lossesOther = 0;
	int draws = 0;
	int nc = 0;

	int calculateTotalWins() {
		this.wins = this.winsDec + this.winsKo + this.winsTko + this.winsOther + this.winsSub;
		return this.wins;
	}

	int calculateTotalLosses() {
		this.losses = this.lossesDec + this.lossesKo + this.lossesTko + this.lossesOther + this.lossesSub;
		return this.wins;
	}

}
