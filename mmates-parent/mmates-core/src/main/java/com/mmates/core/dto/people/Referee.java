package com.mmates.core.dto.people;

import com.mmates.core.dto.sources.SourceInformation;
import io.swagger.annotations.ApiModel;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ApiModel(description = "Referee details")
public class Referee {

	UUID id;
	String name;
	Map<SourceInformation, String> links;


}
