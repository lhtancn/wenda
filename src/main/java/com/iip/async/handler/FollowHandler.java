package com.iip.async.handler;

import com.iip.async.EventHandler;
import com.iip.async.EventModel;
import com.iip.async.EventType;
import com.iip.model.EntityType;
import com.iip.model.Message;
import com.iip.service.MessageService;
import com.iip.service.UserService;
import com.iip.util.WendaUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Demo on 4/16/2017.
 */
@Component
public class FollowHandler implements EventHandler {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Override
    public void doHandler(EventModel eventModel) {
        Message message = new Message();
        message.setCreatedDate(new Date());
        message.setFromId(WendaUtil.SYSTEM_USERID);
        message.setToId(eventModel.getEntityOwnerId());
        message.setHasRead(0);
        if(eventModel.getEntityType() == EntityType.ENTITY_QUESTION) {
            message.setContent("user " + userService.getUser(eventModel.getActorId()).getName() + " 关注了你的问题，" +
                    "http://127.0.0.1:8080/question/" + eventModel.getEntityId());
        }else if(eventModel.getEntityType() == EntityType.ENTITY_USER){
            message.setContent("user " + userService.getUser(eventModel.getActorId()).getName() + " 关注了你，" +
            "http://127.0.0.1:8080/user/" + eventModel.getActorId());
        }

        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.FOLLOW);
    }
}
