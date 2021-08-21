package com.mmates.core.dto.fights;

public enum WinMethod {

	KO, TKO, DECISION, SUBMISSION, OTHER;

	public static WinMethod defineWinMethod(String method) {
		switch (method.toUpperCase().trim()) {
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
			default:
				return OTHER;
		}
	}

}
