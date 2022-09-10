package com.georgeisaev.mmates.sherdog.parser.service;

import com.georgeisaev.mmates.sherdog.domain.Fighter;
import reactor.core.publisher.Mono;

public interface FighterService {

  Mono<Fighter> save(Mono<Fighter> fighterDto);

  Mono<Fighter> findById(String fighterId);
}
