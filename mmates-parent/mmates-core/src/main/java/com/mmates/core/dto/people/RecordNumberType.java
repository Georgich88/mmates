package com.mmates.core.dto.people;


import com.mmates.core.dto.fights.Fight;
import com.mmates.core.dto.fights.FightResult;
import com.mmates.core.dto.fights.WinMethod;

public enum RecordNumberType {

	WINS_KO, WINS_TKO, WINS_SUB, WINS_DEC, WINS_OTHER, LOSSES_KO, LOSSES_TKO, LOSSES_SUB, LOSSES_DEC, LOSSES_OTHER, DRAWS, NO_CONTEST;

	private static final String ERROR_MESSAGE_FIGHTER_FIGHT_DO_NOT_MATCH = "%s did not contest in fight: %s";

	/**
	 * Define how the exact fighter ends exact fight
	 *
	 * @param fight   - the fight.
	 * @param fighter - the fighter, should be one of two fight fighters.
	 * @return type of record
	 */
	public static RecordNumberType defineRecordNumberType(Fight fight, Fighter fighter) {

		var fightResult = fight.getResult();
		var winMethod = fight.getWinMethod();
		var firstFighter = fight.getFirstFighter();
		var secondFighter = fight.getSecondFighter();

		if (!fighter.equals(firstFighter) && !fighter.equals(secondFighter)) {
			throw new IllegalArgumentException(String.format(ERROR_MESSAGE_FIGHTER_FIGHT_DO_NOT_MATCH, fighter, fight));
		}

		if (fightResult == FightResult.DRAW) {
			return DRAWS;
		} else if (fightResult == FightResult.NO_CONTEST) {
			return NO_CONTEST;
		} else {
			final boolean fighterWins = fighter.equals(firstFighter) && fightResult == FightResult.FIGHTER_1_WIN
					|| fighter.equals(secondFighter) && fightResult == FightResult.FIGHTER_2_WIN;
			final boolean fighterLosses = fighter.equals(firstFighter) && fightResult == FightResult.FIGHTER_2_WIN
					|| fighter.equals(secondFighter) && fightResult == FightResult.FIGHTER_1_WIN;
			if (fighterWins
					&& winMethod == WinMethod.KO) {
				return WINS_KO;
			} else if (fighterWins
					&& winMethod == WinMethod.TKO) {
				return WINS_TKO;
			} else if (fighterWins
					&& winMethod == WinMethod.SUBMISSION) {
				return WINS_SUB;
			} else if (fighterWins
					&& winMethod == WinMethod.DECISION) {
				return WINS_DEC;
			} else if (fighterWins
					&& winMethod == WinMethod.OTHER) {
				return WINS_OTHER;
			} else if (fighterLosses
					&& winMethod == WinMethod.KO) {
				return LOSSES_KO;
			} else if (fighterLosses
					&& winMethod == WinMethod.TKO) {
				return LOSSES_TKO;
			} else if (fighterLosses
					&& winMethod == WinMethod.SUBMISSION) {
				return LOSSES_SUB;
			} else if (fighterLosses
					&& winMethod == WinMethod.DECISION) {
				return LOSSES_DEC;
			} else if (fighterLosses
					&& winMethod == WinMethod.OTHER) {
				return LOSSES_OTHER;
			} else {
				throw new IllegalArgumentException("Cannot calculate Record number type");
			}
		}
	}


}

