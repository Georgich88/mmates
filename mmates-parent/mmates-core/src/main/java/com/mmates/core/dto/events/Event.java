package com.mmates.core.model.events;


import com.mmates.core.dto.fights.Fight;
import com.mmates.core.dto.promotion.Promotion;
import com.mmates.core.dto.sources.SourceInformation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ApiModel(description = "Event details")
public class Event {

	@ApiModelProperty(value = "Id")
	UUID id;

	@ApiModelProperty(value = "Name")
	String name;

	@ApiModelProperty(value = "Promotion")
	Promotion promotion;

	@ApiModelProperty(value = "Ownership")
	String ownership;

	@ApiModelProperty(value = "Date")
	LocalDateTime date;

	@ApiModelProperty(value = "Fights")
	List<Fight> fights;

	@ApiModelProperty(value = "Location")
	String location;

	@ApiModelProperty(value = "Venue")
	String venue;

	@ApiModelProperty(value = "Enclosure")
	String enclosure;

	@ApiModelProperty(value = "Links")
	Map<SourceInformation, String> links;

}
