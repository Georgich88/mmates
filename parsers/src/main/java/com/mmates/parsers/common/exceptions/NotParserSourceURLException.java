package com.mmates.parsers.common.exceptions;

import com.mmates.parsers.common.exceptions.ParserException;

public class NotParserSourceURLException extends ParserException {
    public NotParserSourceURLException() {
        super("The url isn't connecting to sherdog domain expected format: http://www.sherdog.com/xxxxxxxx");
    }
}
