package com.georgeisaev.mmates.sherdog.parser.service.impl;

import com.georgeisaev.mmates.sherdog.domain.Fighter;
import com.georgeisaev.mmates.sherdog.parser.data.mapper.FighterMapper;
import com.georgeisaev.mmates.sherdog.parser.repository.FighterRepository;
import com.georgeisaev.mmates.sherdog.parser.service.FighterService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
@Service
public class FighterServiceImpl implements FighterService {

  // Repositories
  FighterRepository fighterRepository;
  // Mappers
  FighterMapper fighterMapper;

  @Override
  public Mono<Fighter> save(Mono<Fighter> fighter) {
    return fighter
        .map(fighterMapper::toEntity)
        .flatMap(fighterRepository::save)
        .map(fighterMapper::toDto);
  }

  @Override
  public Mono<Fighter> findById(String fighterId) {
    return fighterRepository.findById(fighterId).map(fighterMapper::toDto);
  }
}
