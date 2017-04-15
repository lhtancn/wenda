package com.iip.async;

/**
 * Created by Demo on 4/15/2017.
 */
public enum EventType {
    LIKE(0);

    private int value;
    EventType(int value) {
        this.value = value;
    }
    public int getValue() {
        return value;
    }
}
