package com.georgeisaev.mmates.sherdog.parser.service.impl;

import com.georgeisaev.mmates.common.parser.exception.ParserException;
import com.georgeisaev.mmates.sherdog.domain.Fighter;
import com.georgeisaev.mmates.sherdog.domain.FighterRecord;
import com.georgeisaev.mmates.sherdog.parser.command.fighter.FighterParserCommand;
import com.georgeisaev.mmates.sherdog.parser.command.fighter.FighterFightsParserCommand;
import com.georgeisaev.mmates.sherdog.parser.command.fighter.FighterRecordParserCommand;
import com.georgeisaev.mmates.sherdog.parser.service.FighterParserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.stream.Stream;

import static com.georgeisaev.mmates.common.parser.utils.CommonParserUtils.parseDocument;
import static com.georgeisaev.mmates.sherdog.parser.utils.Jsoup2SherdogParserUtils.applyParserCommands;
import static com.georgeisaev.mmates.sherdog.parser.utils.SherdogParserUtils.defineIdFromSherdogUrl;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Slf4j
public class FighterParserServiceImpl implements FighterParserService {

  @Override
  public Fighter parse(final String url) throws IOException, ParserException {
    log.info("Start. Parse Fighter from {}", url);

    val fighterParserCommands =
        Stream.concat(
                FighterParserCommand.availableCommands().stream(),
                FighterFightsParserCommand.availableCommands().stream())
            .toList();
    val recordParserCommands = FighterRecordParserCommand.availableCommands();
    val doc = parseDocument(url);
    val builder = applyParserCommands(doc, Fighter.builder(), fighterParserCommands);
    val fighterRecord =
        applyParserCommands(doc, FighterRecord.builder(), recordParserCommands).build();
    builder.sherdogUrl(url).id(defineIdFromSherdogUrl(url)).fighterRecord(fighterRecord);
    val fighter = builder.build().postConstruct();

    log.info("End. Parse Fighter from {}", url);

    return fighter;
  }
}
