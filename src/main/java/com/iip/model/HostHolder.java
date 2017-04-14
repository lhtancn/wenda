package com.iip.model;

import org.springframework.stereotype.Component;

/**
 * Created by Demo on 4/13/2017.
 */

@Component
public class HostHolder {
    private static ThreadLocal<User> users = new ThreadLocal<>();

    public User getUser() {
        return users.get();
    }

    public void setUser(User user) {
        users.set(user);
    }

    public void clear() {
        users.remove();
    }
}
