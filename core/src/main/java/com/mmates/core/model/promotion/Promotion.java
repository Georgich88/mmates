package com.mmates.core.model.promotion;

import com.mmates.core.model.Loadable;
import com.mmates.core.model.events.Event;

import java.util.ArrayList;
import java.util.List;


public class Promotion implements Loadable {

	private String id;
	private String name;

	private List<Event> events = new ArrayList<>();


}
