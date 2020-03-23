package mmates.core.model.events;

import mmates.core.model.fights.Fight;
import mmates.core.model.promotion.Promotion;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Event {

	private Promotion promotion;
	private Date date;
	private List<Fight> fights = new ArrayList<>();
	private String location = "";


}
