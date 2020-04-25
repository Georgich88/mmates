package com.mmates.core.model.events;

import com.mmates.core.model.Loadable;
import com.mmates.core.model.fights.Fight;
import com.mmates.core.model.promotion.Promotion;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Event implements Loadable {

	private String name;
	private Promotion promotion;
	private Date date;
	private List<Fight> fights = new ArrayList<>();
	private String location = "";

	// Getters and setters

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

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<Fight> getFights() {
		return fights;
	}

	public void setFights(List<Fight> fights) {
		this.fights = fights;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
}
