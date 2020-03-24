package mmates.core.model.people;

import mmates.core.model.fights.Fight;
import mmates.core.model.fights.FightResult;
import mmates.core.model.fights.WinMethod;

import java.util.StringJoiner;

public enum RecordNumberType {

    WINS_KO, WINS_SUB, WINS_DEC, WINS_OTHER, LOSSES_KO, LOSSES_SUB, LOSSES_DEC, LOSSES_OTHER, DRAWS, NO_CONTEST;

    private static final String ERROR_MESSAGE_FIGHTER_FIGHT_DO_NOT_MATCH = "%s did not contest in fight: %s";

    /** Define how the exact fighter ends exact fight
     * @param fight - the fight.
     * @param fighter - the fighter, should be one of two fight fighters.
     * @return type of record
     */
    public static RecordNumberType defineRecordNumberType(Fight fight, Fighter fighter) {

        var fightResult = fight.getResult();
        var winMethod = fight.getWinMethod();
        var firstFighter = fight.getFighter1();
        var secondFighter = fight.getFighter2();

        if (!fighter.equals(firstFighter) && !fighter.equals(secondFighter)) {
            throw new IllegalArgumentException(String.format(ERROR_MESSAGE_FIGHTER_FIGHT_DO_NOT_MATCH, fighter, fight));
        }

        if (fightResult == FightResult.DRAW) {
            return DRAWS;
        } else if (fightResult == FightResult.NO_CONTEST) {
            return NO_CONTEST;
        } else if ((fighter.equals(firstFighter) && fightResult == FightResult.FIGHTER_1_WIN
                || fighter.equals(secondFighter) && fightResult == FightResult.FIGHTER_2_WIN)
                && winMethod == WinMethod.KO) {
            return WINS_KO;
        } else if ((fighter.equals(firstFighter) && fightResult == FightResult.FIGHTER_1_WIN
                || fighter.equals(secondFighter) && fightResult == FightResult.FIGHTER_2_WIN)
                && winMethod == WinMethod.SUBMISSION) {
            return WINS_SUB;
        } else if ((fighter.equals(firstFighter) && fightResult == FightResult.FIGHTER_1_WIN
                || fighter.equals(secondFighter) && fightResult == FightResult.FIGHTER_2_WIN)
                && winMethod == WinMethod.DECISION) {
            return WINS_DEC;
        }else if ((fighter.equals(firstFighter) && fightResult == FightResult.FIGHTER_1_WIN
                || fighter.equals(secondFighter) && fightResult == FightResult.FIGHTER_2_WIN)
                && winMethod == WinMethod.OTHER) {
            return WINS_OTHER;
        } else if ((fighter.equals(firstFighter) && fightResult == FightResult.FIGHTER_2_WIN
                || fighter.equals(secondFighter) && fightResult == FightResult.FIGHTER_1_WIN)
                && winMethod == WinMethod.KO) {
            return LOSSES_KO;
        } else if ((fighter.equals(firstFighter) && fightResult == FightResult.FIGHTER_2_WIN
                || fighter.equals(secondFighter) && fightResult == FightResult.FIGHTER_1_WIN)
                && winMethod == WinMethod.SUBMISSION) {
            return LOSSES_SUB;
        } else if ((fighter.equals(firstFighter) && fightResult == FightResult.FIGHTER_2_WIN
                || fighter.equals(secondFighter) && fightResult == FightResult.FIGHTER_1_WIN)
                && winMethod == WinMethod.DECISION) {
            return LOSSES_DEC;
        } else if ((fighter.equals(firstFighter) && fightResult == FightResult.FIGHTER_2_WIN
                || fighter.equals(secondFighter) && fightResult == FightResult.FIGHTER_1_WIN)
                && winMethod == WinMethod.OTHER) {
            return LOSSES_OTHER;
        } else {
            return null; // TODO: should not return null;
        }



    }


}
