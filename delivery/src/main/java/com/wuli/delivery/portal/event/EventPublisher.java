package com.wuli.delivery.portal.event;

import android.content.Context;

public class EventPublisher {

    private static final String TAG = "Portal.EventPublisher";

    public static final String ACTION_LOGIN = "com.pagoda.buy.ACTION_LOGIN";

    private Context context;

    private static EventPublisher instance = new EventPublisher();

    private EventPublisher() {
    }

    public static EventPublisher getInstance() {
        return instance;
    }

    public void init(Context context) {
        this.context = context;
    }

    public static String getTag() {
        return TAG;
    }

    public void register(Object obj) {
        BusProvider.register(obj);
    }

    public void unRegister(Object obj) {
        BusProvider.unRegister(obj);
    }

    public void sendInsertDataSuccEvent(Event.InsertDataSuccEvent event) {
        BusProvider.post(event);
    }
}
