package com.mmates.core.model;

import com.mmates.core.model.sources.SourceInformation;

public interface Loadable {

    default void setSherdogUrl(String url){
        this.setUrl(SourceInformation.SHERDOG, url);
    }

    default String getSherdogUrl(){
        return this.getUrl(SourceInformation.SHERDOG);
    }

    default void setFacebookUrl(String url){
        this.setUrl(SourceInformation.FACEBOOK, url);
    }

    default String getFacebookUrl(){
        return this.getUrl(SourceInformation.FACEBOOK);
    }

    default void setTapologyUrl(String url){
        this.setUrl(SourceInformation.TAPOLOGY, url);
    }

    default String getTapologyUrl(){
        return this.getUrl(SourceInformation.TAPOLOGY);
    }

    String getUrl(SourceInformation sourceInformation);

    void setUrl(SourceInformation sourceInformation, String url);

}
