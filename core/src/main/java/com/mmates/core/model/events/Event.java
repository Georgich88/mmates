package com.mmates.core.model.events;

import com.mmates.core.model.Loadable;
import com.mmates.core.model.fights.Fight;
import com.mmates.core.model.promotion.Promotion;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Event implements Loadable {

	private Promotion promotion;
	private Date date;
	private List<Fight> fights = new ArrayList<>();
	private String location = "";

}
