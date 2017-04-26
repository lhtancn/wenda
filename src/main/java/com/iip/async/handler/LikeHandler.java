package com.iip.async.handler;

import com.iip.async.EventModel;
import com.iip.async.EventType;
import com.iip.async.EventHandler;
import com.iip.model.Message;
import com.iip.model.User;
import com.iip.service.LikeService;
import com.iip.service.MessageService;
import com.iip.service.UserService;
import com.iip.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Demo on 4/15/2017.
 */
@Component
public class LikeHandler implements EventHandler {
//    @Autowired
//    private LikeService likeService;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;


    @Override
    public void doHandler(EventModel eventModel) {
        Message message = new Message();
        message.setHasRead(0);
        message.setFromId(WendaUtil.SYSTEM_USERID);
        message.setToId(eventModel.getEntityOwnerId());
        message.setCreatedDate(new Date());
        User user = userService.getUser(eventModel.getActorId());
        String content = "user " + user.getName() + " likes your comment. " +
                "http://127.0.0.1:8080/question/" + eventModel.getExts("questionId");
        message.setContent(content);
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
