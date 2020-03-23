package mmates.core.model.people;

import mmates.core.model.fights.Fight;

import java.util.Date;

public class Record {

    private Fighter fighter;
    private Fight fight;
    private Date date;

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

    public Record(Fighter fighter) {
        this.fighter = fighter;
    }

    public Record(Fighter fighter, Record lastRecord) {

        this.fighter = fighter;
        this.date = fight.getDate();

        wins = lastRecord.wins;
        winsKo = lastRecord.winsKo;
        winsSub = lastRecord.winsSub;
        winsDec = lastRecord.winsDec;
        winsOther = lastRecord.winsOther;
        losses = lastRecord.losses;
        lossesKo = lastRecord.lossesKo;
        lossesSub = lastRecord.lossesSub;
        lossesDec = lastRecord.lossesDec;
        lossesOther = lastRecord.lossesOther;
        draws = lastRecord.draws;
        nc = lastRecord.nc;
    }

    public Record setWins(int wins) {
        this.wins = wins;
        return this;
    }

    public Record setWinsKo(int winsKo) {
        this.winsKo = winsKo;
        return this;
    }

    public Record setWinsSub(int winsSub) {
        this.winsSub = winsSub;
        return this;
    }

    public Record setWinsDec(int winsDec) {
        this.winsDec = winsDec;
        return this;
    }

    public Record setWinsOther(int winsOther) {
        this.winsOther = winsOther;
        return this;
    }

    public Record setLosses(int losses) {
        this.losses = losses;
        return this;
    }

    public Record setLossesKo(int lossesKo) {
        this.lossesKo = lossesKo;
        return this;
    }

    public Record setLossesSub(int lossesSub) {
        this.lossesSub = lossesSub;
        return this;
    }

    public Record setLossesDec(int lossesDec) {
        this.lossesDec = lossesDec;
        return this;
    }

    public Record setLossesOther(int lossesOther) {
        this.lossesOther = lossesOther;
        return this;
    }

    public Record setDraws(int draws) {
        this.draws = draws;
        return this;
    }

    public Record setNc(int nc) {
        this.nc = nc;
        return this;
    }
}
