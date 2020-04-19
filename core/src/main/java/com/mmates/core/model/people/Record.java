package com.mmates.core.model.people;

import com.mmates.core.model.fights.Fight;

import java.util.Date;

public class Record {

    private Fighter fighter;
    private Fight fight;
    private Date date;

    private int wins = 0;
    private int winsKo = 0;
    private int winsTko = 0;
    private int winsSub = 0;
    private int winsDec = 0;
    private int winsOther = 0;
    private int losses = 0;
    private int lossesKo = 0;
    private int lossesTko = 0;
    private int lossesSub = 0;
    private int lossesDec = 0;
    private int lossesOther = 0;
    private int draws = 0;
    private int nc = 0;


    public Record() {

    }
    public Record(Fighter fighter) {
        this.fighter = fighter;
    }

    public Record(Fighter fighter, Record lastRecord) {

        this.fighter = fighter;
        this.date = fight.getDate();

        wins = lastRecord.wins;
        winsKo = lastRecord.winsKo;
        winsTko = lastRecord.winsTko;
        winsSub = lastRecord.winsSub;
        winsDec = lastRecord.winsDec;
        winsOther = lastRecord.winsOther;
        losses = lastRecord.losses;
        lossesKo = lastRecord.lossesKo;
        lossesTko = lastRecord.lossesTko;
        lossesSub = lastRecord.lossesSub;
        lossesDec = lastRecord.lossesDec;
        lossesOther = lastRecord.lossesOther;
        draws = lastRecord.draws;
        nc = lastRecord.nc;
    }

    public Record addWinsKo(int winsKo) {
        this.winsKo += winsKo;
        calculateTotalWins();
        return this;
    }

    public Record addWinsTko(int winsTko) {
        this.winsTko += winsTko;
        calculateTotalWins();
        return this;
    }

    public Record addWinsSub(int winsSub) {
        this.winsSub += winsSub;
        calculateTotalWins();
        return this;
    }

    public Record addWinsDec(int winsDec) {
        this.winsDec += winsDec;
        calculateTotalWins();
        return this;
    }

    public Record addWinsOther(int winsOther) {
        this.winsOther += winsOther;
        calculateTotalWins();
        return this;
    }

    public Record addLossesKo(int lossesKo) {
        this.lossesKo += lossesKo;
        calculateTotalLosses();
        return this;
    }

    public Record addLossesTko(int lossesTko) {
        this.lossesTko += lossesTko;
        calculateTotalLosses();
        return this;
    }

    public Record addLossesSub(int lossesSub) {
        this.lossesSub += lossesSub;
        calculateTotalLosses();
        return this;
    }

    public Record addLossesDec(int lossesDec) {
        this.lossesDec += lossesDec;
        calculateTotalLosses();
        return this;
    }

    public Record addLossesOther(int lossesOther) {
        this.lossesOther += lossesOther;
        calculateTotalLosses();
        return this;
    }

    public Record addDraws(int draws) {
        this.draws += draws;
        return this;
    }

    public Record addNc(int nc) {
        this.nc += nc;
        return this;
    }

    private int calculateTotalWins() {
        this.wins = this.winsDec + this.winsKo + this.winsTko + this.winsOther + this.winsSub;
        return this.wins;
    }

    private int calculateTotalLosses() {
        this.losses = this.lossesDec + this.lossesKo + this.lossesTko + this.lossesOther + this.lossesSub;
        return this.wins;
    }

}
