package com.mmates.core.model.teams;

import com.mmates.core.model.Loadable;
import com.mmates.core.model.sources.SourceInformation;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;

public class Team implements Loadable {

    private UUID id;
    private String name;
    private Map<SourceInformation, String> profiles = new HashMap<>();

    // Profiles URLs

    @Override
    public String getUrl(SourceInformation sourceInformation) {
        return profiles.getOrDefault(sourceInformation, "");
    }

    @Override
    public void setUrl(SourceInformation sourceInformation, String url) {
        profiles.put(sourceInformation, url);
    }

    // Object inherited methods

    @Override
    public String toString() {
        return new StringJoiner(", ", Team.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .toString();
    }

}
