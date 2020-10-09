package com.mmates.core.model.events;

import com.mmates.core.model.Loadable;
import com.mmates.core.model.fights.Fight;
import com.mmates.core.model.promotion.Promotion;
import com.mmates.core.model.sources.SourceInformation;

import java.time.ZonedDateTime;
import java.util.*;

public class Event implements Loadable {

	// Fields

    private UUID id;
    private String name;
    private Promotion promotion;
    private String ownership;
    private ZonedDateTime date;
    private List<Fight> fights = new ArrayList<>();
    private String location = "";
    private String venue = "";
    private String enclosure = "";
    private Map<SourceInformation, String> profiles;

	// Constructors

    public Event() {
		profiles = new EnumMap<>(SourceInformation.class);
    }

	// Profiles URLs

    @Override
    public String getUrl(SourceInformation sourceInformation) {
        return profiles.getOrDefault(sourceInformation, "");
    }

    // Constructors

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

    public Promotion getPromotion() {
        return promotion;
    }

    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public List<Fight> getFights() {
        return fights;
    }

    public void setFights(List<Fight> fights) {
        this.fights = fights;
    }

    public String getEnclosure() {
        return enclosure;
    }

    public void setEnclosure(String enclosure) {
        this.enclosure = enclosure;
    }

    public String getOwnership() {
        return ownership;
    }

    public void setOwnership(String ownership) {
        this.ownership = ownership;
    }

    public Map<SourceInformation, String> getProfiles() {
        return profiles;
    }

    public void setProfiles(Map<SourceInformation, String> profiles) {
        this.profiles = profiles;
    }

    public void addProfiles(Map<SourceInformation, String> profiles) {
        this.profiles.putAll(profiles);
    }

    // Object inherited methods

    @Override
    public String toString() {
        return new StringJoiner(", ", Event.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("promotion=" + promotion)
                .add("date=" + date)
                .add("location='" + location + "'")
                .add("profiles=" + profiles)
                .toString();
    }



}
