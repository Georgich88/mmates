package com.georgeisaev.mmates.sherdog.parser.service;


import com.georgeisaev.mmates.common.parser.exception.ParserException;

import java.io.IOException;

public interface ParserService<T> {

    /**
     * Parses a page by url
     *
     * @param url of the site page
     * @return the object parsed by the parser
     * @throws IOException     if connecting to Sherdog fails
     * @throws ParserException if anything related to the parser goes wrong
     */
    T parse(String url) throws IOException, ParserException;

}
