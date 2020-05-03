package com.mmates.core.model.fights;

public enum WinMethod {

    KO, TKO, DECISION, SUBMISSION, OTHER;

    public static WinMethod defineWinMethod(String method){

        String methodUpperCase = method.toUpperCase();
        if (methodUpperCase.equals("KO") || methodUpperCase.equals("KNOCKOUT") || methodUpperCase.equals("K.O.")) return KO;
        else if (methodUpperCase.equals("TKO") || methodUpperCase.equals("TECHNICAL KNOCKOUT") || methodUpperCase.equals("T.K.O.")) return TKO;
        else if (methodUpperCase.equals("DECISION") || methodUpperCase.equals("DEC")) return DECISION;
        else if (methodUpperCase.equals("SUBMISSION") || methodUpperCase.equals("SUB")) return SUBMISSION;
        else return OTHER;
    }

}
