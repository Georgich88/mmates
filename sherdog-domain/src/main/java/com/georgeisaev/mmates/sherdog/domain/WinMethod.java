package com.georgeisaev.mmates.sherdog.domain;

import java.util.NoSuchElementException;

public enum WinMethod {
  KO,
  TKO,
  DECISION,
  SUBMISSION,
  TECHNICAL_SUBMISSION,
  NC,
  OTHER;

  public static WinMethod winMethod(final String methodAndDetails) {
    final String[] methodDetails = methodAndDetails.split("[()]");
    if (methodDetails.length == 0) {
      throw new NoSuchElementException("Cannot define win method from " + methodAndDetails);
    }
    final String methodUpperCase = methodDetails[0].toUpperCase().trim();
    switch (methodUpperCase) {
      case "KO":
      case "KNOCKOUT":
      case "K.O.":
        return KO;
      case "TKO":
      case "TECHNICAL KNOCKOUT":
      case "T.K.O.":
        return TKO;
      case "DECISION":
      case "DEC":
        return DECISION;
      case "SUBMISSION":
      case "SUB":
        return SUBMISSION;
      case "TECHNICAL SUBMISSION":
        return TECHNICAL_SUBMISSION;
      case "NO CONTEST":
      case "NC":
      case "N/C":
        return NC;
      default:
        return OTHER;
    }
  }

  public static String winMethodDetails(final String methodAndDetails) {
    final String[] methodDetails = methodAndDetails.split("[()]");
    if (methodDetails.length < 2) {
      throw new NoSuchElementException("Cannot define win method details from " + methodAndDetails);
    }
    return methodDetails[1].trim();
  }
}
