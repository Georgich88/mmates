package mmates.core.model.fights;

import mmates.core.model.events.Event;
import mmates.core.model.people.Fighter;

import java.util.Date;

public class Fight {

    private String name;
    private Event event;
    private Fighter fighter1;
    private Fighter fighter2;
    private Date date;
    private FightResult result = FightResult.NOT_HAPPENED;
    private WinMethod winMethod;
    private float winTime;
    private int winRound;
    private FightType type;

    private String twitter;
    private String facebook;
    private String instagram;


    public Fight(float winTime) {
        this.winTime = winTime;
    }

    public Date getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public Fight setName(String name) {
        this.name = name;
        return this;
    }

    public Event getEvent() {
        return event;
    }

    public Fight setEvent(Event event) {
        this.event = event;
        return this;
    }

    public Fighter getFighter1() {
        return fighter1;
    }

    public Fight setFighter1(Fighter fighter1) {
        this.fighter1 = fighter1;
        return this;
    }

    public Fighter getFighter2() {
        return fighter2;
    }

    public Fight setFighter2(Fighter fighter2) {
        this.fighter2 = fighter2;
        return this;
    }

    public Fight setDate(Date date) {
        this.date = date;
        return this;
    }

    public FightResult getResult() {
        return result;
    }

    public Fight setResult(FightResult result) {
        this.result = result;
        return this;
    }

    public WinMethod getWinMethod() {
        return winMethod;
    }

    public Fight setWinMethod(WinMethod winMethod) {
        this.winMethod = winMethod;
        return this;
    }

    public float getWinTime() {
        return winTime;
    }

    public Fight setWinTime(float winTime) {
        this.winTime = winTime;
        return this;
    }

    public int getWinRound() {
        return winRound;
    }

    public Fight setWinRound(int winRound) {
        this.winRound = winRound;
        return this;
    }

    public FightType getType() {
        return type;
    }

    public Fight setType(FightType type) {
        this.type = type;
        return this;
    }
}
