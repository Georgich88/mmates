package com.georgeisaev.mmates.sherdog.parser.controller;

import com.georgeisaev.mmates.sherdog.domain.Fighter;
import com.georgeisaev.mmates.sherdog.parser.service.FighterParserService;
import com.georgeisaev.mmates.sherdog.parser.service.FighterService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.text.ParseException;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
@RestController("/fighters")
public class FighterController {

  FighterService fighterService;
  FighterParserService fighterParserService;

  @PostMapping
  public Publisher<ResponseEntity<Fighter>> save(@RequestBody Mono<Fighter> fighterDto) {
    return fighterService
        .save(fighterDto)
        .map(f -> ResponseEntity.created(URI.create("/fighters/" + f.getId())).body(f));
  }

  @GetMapping("/{fighterId}")
  public Publisher<ResponseEntity<Fighter>> findById(@PathVariable String fighterId) {
    return fighterService.findById(fighterId).map(ResponseEntity::ok);
  }

  @PostMapping("/parsings")
  public Publisher<ResponseEntity<Fighter>> parse(@RequestBody String url) throws IOException {
    return Mono.just(ResponseEntity.ok(fighterParserService.parse(url)));
  }

}
