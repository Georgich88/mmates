package com.georgeisaev.mmates.sherdog.parser.data.mapper;

import com.georgeisaev.mmates.sherdog.domain.Fighter;
import com.georgeisaev.mmates.sherdog.parser.data.document.FighterDoc;
import org.mapstruct.Mapper;

@Mapper(imports = {FightMapper.class})
public interface FighterMapper {

  FighterDoc toEntity(Fighter model);

  Fighter toDto(FighterDoc dto);
}
