package com.wuli.delivery.portal.event;

public class Event {

    private final static Event Event = new Event();

    public static Event getInstance() {
        return Event;
    }

    private Event() {

    }

    public final static class InsertDataSuccEvent {

    }

}
