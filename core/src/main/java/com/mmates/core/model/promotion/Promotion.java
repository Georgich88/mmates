package com.mmates.core.model.promotion;

import com.mmates.core.model.Loadable;
import com.mmates.core.model.events.Event;
import com.mmates.core.model.sources.SourceInformation;

import java.util.*;

public class Promotion implements Loadable {

    private UUID id;
    private String name;
    private List<Event> events = new ArrayList<>();
    private Map<SourceInformation, String> profiles = new EnumMap<>(SourceInformation.class);

    // Constructors

    public Promotion() {
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

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
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
        return new StringJoiner(", ", Promotion.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("events=" + events)
                .toString();
    }

}
