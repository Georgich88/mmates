package com.mmates.core.model.fights;

import com.mmates.core.model.Loadable;
import com.mmates.core.model.events.Event;
import com.mmates.core.model.people.Fighter;

import java.util.Date;

public class Fight implements Loadable {

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


    public Fight() {
    }

    // Getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Fighter getFighter1() {
        return fighter1;
    }

    public void setFighter1(Fighter fighter1) {
        this.fighter1 = fighter1;
    }

    public Fighter getFighter2() {
        return fighter2;
    }

    public void setFighter2(Fighter fighter2) {
        this.fighter2 = fighter2;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public FightResult getResult() {
        return result;
    }

    public void setResult(FightResult result) {
        this.result = result;
    }

    public WinMethod getWinMethod() {
        return winMethod;
    }

    public void setWinMethod(WinMethod winMethod) {
        this.winMethod = winMethod;
    }

    public float getWinTime() {
        return winTime;
    }

    public void setWinTime(float winTime) {
        this.winTime = winTime;
    }

    public int getWinRound() {
        return winRound;
    }

    public void setWinRound(int winRound) {
        this.winRound = winRound;
    }

    public FightType getType() {
        return type;
    }

    public void setType(FightType type) {
        this.type = type;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instagram) {
        this.instagram = instagram;
    }
}
