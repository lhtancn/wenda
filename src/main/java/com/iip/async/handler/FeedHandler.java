package com.iip.async.handler;

import com.alibaba.fastjson.JSONObject;
import com.iip.async.EventHandler;
import com.iip.async.EventModel;
import com.iip.async.EventType;
import com.iip.model.EntityType;
import com.iip.model.Feed;
import com.iip.model.Question;
import com.iip.model.User;
import com.iip.service.FeedService;
import com.iip.service.FollowService;
import com.iip.service.QuestionService;
import com.iip.service.UserService;
import com.iip.util.JedisAdapter;
import com.iip.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.util.*;

/**
 * Created by Demo on 4/16/2017.
 */
@Component
public class FeedHandler implements EventHandler{

    @Autowired
    private QuestionService questionService;

    @Autowired
    private FeedService feedService;

    @Autowired
    private JedisAdapter jedisAdapter;

    @Autowired
    private UserService userService;

    @Autowired
    private FollowService followService;

    private String buildFeedData(EventModel model) {
        Map<String, String> map = new HashMap<>();

        User user = userService.getUser(model.getActorId());
        if(user == null) {
            return null;
        }

        map.put("userId", String.valueOf(user.getId()));
        map.put("userHead", user.getHeadUrl());
        map.put("userName", user.getName());

        if(model.getType() == EventType.COMMENT || model.getType() == EventType.FOLLOW &&
                model.getEntityType() == EntityType.ENTITY_QUESTION) {
            Question question = questionService.getById(model.getEntityId());
            if(question == null) {
                return null;
            }
            map.put("questionId", String.valueOf(question.getId()));
            map.put("questionTitle", question.getTitle());
            return JSONObject.toJSONString(map);
        }
        return null;
    }

    @Override
    public void doHandler(EventModel eventModel) {
        Feed feed = new Feed();
        feed.setCreatedDate(new Date());
        feed.setType(eventModel.getType().getValue());
        feed.setUserId(eventModel.getActorId());
        feed.setData(buildFeedData(eventModel));
        if(feed.getData() == null) {
            return;
        }
        feedService.addFeed(feed);

        List<Integer> followers = followService.getFollowers(EntityType.ENTITY_USER, eventModel.getActorId(), Integer.MAX_VALUE);
        for(Integer id : followers) {
            String timeLineKey = RedisKeyUtil.getTimeLineKey(id);
            jedisAdapter.lpush(timeLineKey, String.valueOf(feed.getId()));
        }
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(new EventType[]{EventType.COMMENT, EventType.FOLLOW});
    }
}
