package com.mmates.core.model.teams;

import com.mmates.core.model.Loadable;
import com.mmates.core.model.sources.SourceInformation;

import java.util.HashMap;
import java.util.Map;
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

}
