package com.mmates.core.model.people;

import com.mmates.core.model.Loadable;
import com.mmates.core.model.fights.Fight;
import com.mmates.core.model.teams.Team;
import com.mmates.core.model.sources.SourceInformation;

import java.util.*;
import java.util.function.Consumer;

public class Fighter implements Person, Loadable {

    private UUID id;
    private String name;
    private String nickname = "";
    private int height = 0;
    private int reach = 0;
    private int legReach = 0;
    private int weight = 0;
    private Date birthday;
    private Date debut;
    private Team team;
    private int wins = 0;
    private int winsKo = 0;
    private int winsSub = 0;
    private int winsDec = 0;
    private int winsOther = 0;
    private int losses = 0;
    private int lossesKo = 0;
    private int lossesSub = 0;
    private int lossesDec = 0;
    private int lossesOther = 0;
    private int draws = 0;
    private int nc = 0;

    private String picture = "";

    private Map<SourceInformation, String> profiles = new HashMap<>();

    private List<Fight> fights = new ArrayList<>();
    private List<Record> records = new ArrayList<>();

    // Fighter record

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

    // Profiles URL

    public String getUrl(SourceInformation sourceInformation) {
        return profiles.getOrDefault(sourceInformation, "");
    }

    public void setUrl(SourceInformation sourceInformation, String url) {
        profiles.put(sourceInformation, url);
    }

    // Getters and setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getWinsKo() {
        return winsKo;
    }

    public void setWinsKo(int winsKo) {
        this.winsKo = winsKo;
    }

    public int getWinsSub() {
        return winsSub;
    }

    public void setWinsSub(int winsSub) {
        this.winsSub = winsSub;
    }

    public int getWinsDec() {
        return winsDec;
    }

    public void setWinsDec(int winsDec) {
        this.winsDec = winsDec;
    }

    public int getWinsOther() {
        return winsOther;
    }

    public void setWinsOther(int winsOther) {
        this.winsOther = winsOther;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getLossesKo() {
        return lossesKo;
    }

    public void setLossesKo(int lossesKo) {
        this.lossesKo = lossesKo;
    }

    public int getLossesSub() {
        return lossesSub;
    }

    public void setLossesSub(int lossesSub) {
        this.lossesSub = lossesSub;
    }

    public int getLossesDec() {
        return lossesDec;
    }

    public void setLossesDec(int lossesDec) {
        this.lossesDec = lossesDec;
    }

    public int getLossesOther() {
        return lossesOther;
    }

    public void setLossesOther(int lossesOther) {
        this.lossesOther = lossesOther;
    }

    public int getDraws() {
        return draws;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }

    public int getNc() {
        return nc;
    }

    public void setNc(int nc) {
        this.nc = nc;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Map<SourceInformation, String> getProfiles() {
        return profiles;
    }

    public void setProfiles(Map<SourceInformation, String> profiles) {
        this.profiles = profiles;
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
}
