package com.mmates.core.dto.promotion;

import com.mmates.core.dto.sources.SourceInformation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ApiModel(description = "Promotion info")
public class Promotion {

	@ApiModelProperty(value = "Id")
	UUID id;

	@ApiModelProperty(value = "Name")
	String name;

	@ApiModelProperty(value = "Headquarters")
	String headquarters;

	@ApiModelProperty(value = "Acronyms")
	String acronyms;

	@ApiModelProperty(value = "Also Known As")
	String alsoKnownAs;

	@ApiModelProperty(value = "Links")
	Map<SourceInformation, String> links;

}
