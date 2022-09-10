package com.georgeisaev.mmates.sherdog.parser.service.impl;

import com.georgeisaev.mmates.common.parser.command.JsopAttributeParserCommand;
import com.georgeisaev.mmates.common.parser.exception.ParserException;
import com.georgeisaev.mmates.common.parser.utils.CommonParserUtils;
import com.georgeisaev.mmates.sherdog.domain.Fighter;
import com.georgeisaev.mmates.sherdog.domain.FighterRecord;
import com.georgeisaev.mmates.sherdog.parser.data.parser.fighter.FighterAttributeParserCommand;
import com.georgeisaev.mmates.sherdog.parser.data.parser.fighter.FighterFightsAttributeParserCommand;
import com.georgeisaev.mmates.sherdog.parser.data.parser.fighter.FighterRecordAttributeParserCommand;
import com.georgeisaev.mmates.sherdog.parser.service.FighterParserService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Stream;

import static com.georgeisaev.mmates.sherdog.parser.utils.SherdogParserUtils.MSG_ERR_CANNOT_PARSE_PROPERTY;
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
                Stream.concat(FighterAttributeParserCommand.availableCommands().stream(),
                                FighterFightsAttributeParserCommand.availableCommands().stream())
                        .toList();
        val recordParserCommands
                = FighterRecordAttributeParserCommand.availableCommands();

        val doc = CommonParserUtils.parseDocument(url);
        val builder = parse(doc, Fighter.builder(), fighterParserCommands);
        val fighterRecord = parse(doc, FighterRecord.builder(), recordParserCommands).build();

        builder.sherdogUrl(url)
                .id(defineIdFromSherdogUrl(url))
                .fighterRecord(fighterRecord);

        return builder
                .build()
                .postConstruct();
    }

    public <T, C extends JsopAttributeParserCommand<T>> T parse(
            Document source, T target, Collection<C> commands) {
        commands.forEach(c -> {
            try {
                c.parse(source, target);
            } catch (Exception e) {
                log.error(MSG_ERR_CANNOT_PARSE_PROPERTY, c.getAttribute(), target, e);
            }
        });
        return target;
    }

}
