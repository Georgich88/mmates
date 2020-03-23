package mmates.core.model.people;

import mmates.core.model.fights.Fight;
import mmates.core.model.sources.SourceInformation;
import mmates.core.model.teams.Team;

import java.util.*;

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

    List<Fight> fights = new ArrayList<>();
    List<Record> records = new ArrayList<>();

    private void calculateRecordByFights() {
        fights.forEach(fight -> {
            Record lastRecord = null;
            if (this.records.size() != 0) {
                 lastRecord = records.get(records.size() - 1);
            }

            this.addRecord(fight, lastRecord);
        });
    }

    public Record addRecord(Fight fight, Record lastRecord){

        var currentRecord = new Record(this, lastRecord);
        // TODO: implement add figth result to current event
        return currentRecord;
    }


}
