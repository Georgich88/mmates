package com.georgeisaev.mmates.sherdog.parser.data.mapper;

import com.georgeisaev.mmates.sherdog.domain.Fight;
import com.georgeisaev.mmates.sherdog.parser.data.document.FightDoc;
import org.mapstruct.Mapper;

@Mapper
public interface FightMapper {

  FightDoc toEntity(Fight dto);

  Fight toEntity(FightDoc dto);
}
