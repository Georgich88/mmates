package com.georgeisaev.mmates.common.parser.command;

public interface AttributeParserCommand<T, S> {

  void parse(S source, T target);

  String getAttribute();
}
