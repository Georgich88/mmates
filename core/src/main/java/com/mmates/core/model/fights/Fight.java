package com.mmates.core.model.fights;

import com.mmates.core.model.Loadable;
import com.mmates.core.model.events.Event;
import com.mmates.core.model.people.Fighter;
import com.mmates.core.model.sources.SourceInformation;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Fight implements Loadable {

    private UUID id;
    private String name;
    private Event event;
    private Fighter fighter1;
    private Fighter fighter2;
    private ZonedDateTime date;
    private FightResult result = FightResult.NOT_HAPPENED;
    private WinMethod winMethod;
    /**
     * Win time of the round in seconds
     */
    private int winTime;
    private int winRound;
    private FightType type;
    private Map<SourceInformation, String> profiles = new HashMap<>();

    // Constructors

    public Fight() {
    }

    // Profiles URLs

    @Override
    public String getUrl(SourceInformation sourceInformation) {
        return profiles.getOrDefault(sourceInformation, "");
    }

    @Override
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

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
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

    public int getWinTime() {
        return winTime;
    }

    public void setWinTime(int winTime) {
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

    public Map<SourceInformation, String> getProfiles() {
        return profiles;
    }

    public void setProfiles(Map<SourceInformation, String> profiles) {
        this.profiles = profiles;
    }



}
