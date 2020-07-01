package com.mmates.parsers.application;

import com.mmates.core.model.events.Event;
import com.mmates.parsers.common.exceptions.ParserException;
import com.mmates.parsers.sherdog.Sherdog;
import com.mmates.parsers.tapology.Tapology;

import java.io.IOException;
import java.text.ParseException;

public class Application {

    private static Sherdog sherdog;
    private static Tapology tapology;
    static {
        sherdog = new Sherdog.Builder().withTimezone("Asia/Kuala_Lumpur").build();
        tapology = new Tapology.Builder().withTimezone("Asia/Kuala_Lumpur").build();
    }

    public static void main(String[] args) throws ParseException, ParserException, IOException {
        //Event ufc1 = sherdog.getEvent("https://www.sherdog.com/events/UFC-1-The-Beginning-7");
        Event ufc153 = tapology.getEvent("https://www.tapology.com/fightcenter/events/14431-ufc-153");
        //System.out.println(ufc1);
        System.out.println(ufc153);
    }
}
