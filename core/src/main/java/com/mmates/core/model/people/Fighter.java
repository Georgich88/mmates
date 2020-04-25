package com.mmates.core.model.people;

import com.mmates.core.model.Loadable;
import com.mmates.core.model.fights.Fight;
import com.mmates.core.model.teams.Team;
import com.mmates.core.model.sources.SourceInformation;

import java.util.*;
import java.util.function.Consumer;

public class Fighter implements Person, Loadable {

    private String name;
    private String nickname = "";
    private int height = 0;
    private int reach = 0;
    private int legReach = 0;
    private int weight = 0;
    private Date birthday;
    private Date debut;
    private Team team;
    private String picture = "";

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

    // Getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getReach() {
        return reach;
    }

    public void setReach(int reach) {
        this.reach = reach;
    }

    public int getLegReach() {
        return legReach;
    }

    public void setLegReach(int legReach) {
        this.legReach = legReach;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Date getDebut() {
        return debut;
    }

    public void setDebut(Date debut) {
        this.debut = debut;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public List<Fight> getFights() {
        return fights;
    }

    public void setFights(List<Fight> fights) {
        this.fights = fights;
    }

    public List<Record> getRecords() {
        return records;
    }

    public void setRecords(List<Record> records) {
        this.records = records;
    }

    public String getSherdogUrl() {
        return profiles.getOrDefault(SourceInformation.SHERDOG, "");
    }

    public void setSherdogUrl(String sherdogUrl) {
        profiles.put(SourceInformation.SHERDOG, sherdogUrl);
    }
}
