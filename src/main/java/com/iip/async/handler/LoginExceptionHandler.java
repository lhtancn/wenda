package com.iip.async.handler;

import com.iip.async.EventHandler;
import com.iip.async.EventModel;
import com.iip.async.EventType;
import com.iip.util.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Demo on 4/15/2017.
 */
@Component
public class LoginExceptionHandler implements EventHandler{
    @Autowired
    MailSender mailSender;

    @Override
    public void doHandler(EventModel eventModel) {
        Map<String, Object> map = new HashMap<>();
        map.put("username", eventModel.getExts("username"));
        mailSender.sendWithHTMLTemplate(eventModel.getExts("email"), "login exception.",
                "mails/login_exception.html", map);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LOGIN);
    }
}
