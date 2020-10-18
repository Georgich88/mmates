package com.mmates.core.model.fights;

import com.mmates.core.model.Loadable;
import com.mmates.core.model.events.Event;
import com.mmates.core.model.people.Fighter;
import com.mmates.core.model.sources.SourceInformation;

import java.time.ZonedDateTime;
import java.util.EnumMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;

import static com.mmates.core.model.fights.FightResult.NOT_HAPPENED;

public class Fight implements Loadable {

    private UUID id;
    private String name;
    private Event event;
    private Fighter firstFighter;
    private Fighter secondFighter;
    private ZonedDateTime date;
    private FightResult result = NOT_HAPPENED;
    private WinMethod winMethod;
    private int winTime; // in seconds
    private int winRound;
    private FightType type;
    private Map<SourceInformation, String> profiles;

    // Constructors

    public Fight() {
        profiles = new EnumMap<>(SourceInformation.class);
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

    public Fighter getFirstFighter() {
        return firstFighter;
    }

    public void setFirstFighter(Fighter firstFighter) {
        this.firstFighter = firstFighter;
    }

    public Fighter getSecondFighter() {
        return secondFighter;
    }

    public void setSecondFighter(Fighter secondFighter) {
        this.secondFighter = secondFighter;
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

    // Object inherited methods

    @Override
    public String toString() {
        return new StringJoiner(", ", Fight.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("event=" + event)
                .add("firstFighter=" + firstFighter)
                .add("secondFighter=" + secondFighter)
                .add("date=" + date)
                .add("result=" + result)
                .add("winMethod=" + winMethod)
                .add("winTime=" + winTime)
                .add("winRound=" + winRound)
                .add("type=" + type)
                .add("profiles=" + profiles)
                .toString();
    }

}
