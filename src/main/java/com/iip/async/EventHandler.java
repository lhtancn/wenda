package com.iip.async;

import java.util.List;

/**
 * Created by Demo on 4/15/2017.
 */

public interface EventHandler {
    void doHandler(EventModel eventModel);

    List<EventType> getSupportEventTypes();
}
