package mmates.core.model.people;

import mmates.core.model.fights.Fight;
import mmates.core.model.sources.SourceInformation;
import mmates.core.model.teams.Team;

import java.util.*;
import java.util.function.Consumer;

public class Fighter implements Person {

    private String name;
    private String nickname = "";
    private int height = 0;
    private int reach = 0;
    private int legReach = 0;
    private int weight = 0;
    private Date birthday;
    private Date debut;
    private Team team;

    private Map<SourceInformation, String> profiles = new HashMap<>();

    private List<Fight> fights = new ArrayList<>();
    private List<Record> records = new ArrayList<>();

    private void calculateRecordByFights() {
        records.clear();
        fights.forEach(getRecordCalculatorByFight());
    }

    private Consumer<Fight> getRecordCalculatorByFight() {
        return fight -> {
            Record lastRecord = null;
            if (records.size() != 0) {
                lastRecord = records.get(records.size() - 1);
            }
            this.addRecord(fight, lastRecord);
        };
    }

    public Record addRecord(Fight fight, Record lastRecord) {

        var currentRecord = new Record(this, lastRecord);

        int winsKo = (RecordNumberType.defineRecordNumberType(fight, this) == RecordNumberType.WINS_KO) ? 1 : 0;
        int winsSub = (RecordNumberType.defineRecordNumberType(fight, this) == RecordNumberType.WINS_SUB) ? 1 : 0;
        int winsDec = (RecordNumberType.defineRecordNumberType(fight, this) == RecordNumberType.WINS_DEC) ? 1 : 0;
        int winsOther = (RecordNumberType.defineRecordNumberType(fight, this) == RecordNumberType.WINS_OTHER) ? 1 : 0;
        int lossesKo = (RecordNumberType.defineRecordNumberType(fight, this) == RecordNumberType.LOSSES_KO) ? 1 : 0;
        int lossesSub = (RecordNumberType.defineRecordNumberType(fight, this) == RecordNumberType.LOSSES_SUB) ? 1 : 0;
        int lossesDec = (RecordNumberType.defineRecordNumberType(fight, this) == RecordNumberType.LOSSES_DEC) ? 1 : 0;
        int lossesOther = (RecordNumberType.defineRecordNumberType(fight, this) == RecordNumberType.LOSSES_OTHER) ? 1 : 0;
        int draws = (RecordNumberType.defineRecordNumberType(fight, this) == RecordNumberType.DRAWS) ? 1 : 0;
        int nc = (RecordNumberType.defineRecordNumberType(fight, this) == RecordNumberType.NO_CONTEST) ? 1 : 0;

        currentRecord.addWinsKo(winsKo)
                .addWinsSub(winsSub)
                .addWinsDec(winsDec)
                .addWinsOther(winsOther)
                .addLossesKo(lossesKo)
                .addLossesSub(lossesKo)
                .addLossesSub(lossesSub)
                .addLossesDec(lossesDec)
                .addLossesOther(lossesOther)
                .addDraws(draws)
                .addNc(nc);

        this.records.add(currentRecord);
        return currentRecord;
    }


}
