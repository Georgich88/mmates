package com.georgeisaev.mmates.sherdog.parser.repository;

import com.georgeisaev.mmates.sherdog.parser.data.document.FighterDoc;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface FighterRepository extends ReactiveMongoRepository<FighterDoc, String> {}
