package com.mmates.parsers.tapology;

import com.mmates.core.model.Loadable;
import com.mmates.core.model.sources.SourceInformation;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;

public class TapologyBaseObject implements Loadable {

	private String name;
	private String sherdogUrl;
	private LocalDate dateModified;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSherdogUrl() {
		return sherdogUrl;
	}

	@Override
	public String getUrl(SourceInformation sourceInformation) {
		return null;
	}

	@Override
	public void setUrl(SourceInformation sourceInformation, String url) {

	}

	public void setSherdogUrl(String sherdogUrl) {
		this.sherdogUrl = sherdogUrl;
	}

	public LocalDate getDateModified() {
		return dateModified;
	}

	public void setDateModified(LocalDate dateModified) {
		this.dateModified = dateModified;
	}

	public static final String generateIdBySherdogUrl(String sherdogUrl) throws URISyntaxException {
		URI uri = new URI(sherdogUrl);
		String[] segments = uri.getPath().split("/");
		String idStr = segments[segments.length - 1];
		return idStr;
	}

	@Override
	public String toString() {
		return "SherdogBaseObject{" + "name='" + name + '\'' + ", sherdogUrl='" + sherdogUrl + '\'' + '}';
	}
}
