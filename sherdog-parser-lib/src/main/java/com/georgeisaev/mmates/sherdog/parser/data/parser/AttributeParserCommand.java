package com.georgeisaev.mmates.sherdog.parser.data.parser;

public interface AttributeParserCommand<T, S> {

  void parse(S source, T target);

  String getAttribute();
}
