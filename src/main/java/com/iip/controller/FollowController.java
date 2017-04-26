package com.iip.controller;

import com.iip.async.EventModel;
import com.iip.async.EventProducer;
import com.iip.async.EventType;
import com.iip.model.*;
import com.iip.service.CommentService;
import com.iip.service.FollowService;
import com.iip.service.QuestionService;
import com.iip.service.UserService;
import com.iip.util.WendaUtil;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.View;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Demo on 4/16/2017.
 */
@Controller
public class FollowController {
    @Autowired
    private FollowService followService;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @RequestMapping(value = {"/followUser"}, method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String followUser(@RequestParam("userId") int userId) {
        if(hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }

        boolean ret = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId);

        eventProducer.fireEvent(new EventModel(EventType.FOLLOW).setActorId(hostHolder.getUser().getId())
        .setEntityType(EntityType.ENTITY_USER).setEntityId(userId).setEntityOwnerId(userId));

        return WendaUtil.getJSONString(ret ? 0 : 1,
                String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(), EntityType.ENTITY_USER)));
    }

    @RequestMapping(value = {"/unfollowUser"}, method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String unfollowUser(@RequestParam("userId") int userId) {
        if(hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }

        boolean ret = followService.unfollow(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId);

//        eventProducer.fireEvent(new EventModel(EventType.FOLLOW).setActorId(hostHolder.getUser().getId())
//                .setEntityType(EntityType.ENTITY_USER).setEntityId(userId).setEntityOwnerId(userId));

        return WendaUtil.getJSONString(ret ? 0 : 1,
                String.valueOf(followService.getFolloweeCount(hostHolder.getUser().getId(), EntityType.ENTITY_USER)));
    }

    @RequestMapping(value = {"/followQuestion"}, method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String followQuestion(@RequestParam("questionId") int questionId) {
        if(hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }

        Question q = questionService.getById(questionId);
        if(q == null) {
            return WendaUtil.getJSONString(1, "no such question.");
        }

        boolean ret = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, questionId);

        eventProducer.fireEvent(new EventModel(EventType.FOLLOW).setActorId(hostHolder.getUser().getId())
                .setEntityType(EntityType.ENTITY_QUESTION).setEntityId(questionId)
                .setEntityOwnerId(questionService.getById(questionId).getUserId()));


        Map<String, Object> info = new HashMap<>();
        info.put("headUrl", hostHolder.getUser().getHeadUrl());
        info.put("name", hostHolder.getUser().getName());
        info.put("id", hostHolder.getUser().getId());
        info.put("count", followService.getFollowerCount(EntityType.ENTITY_QUESTION, questionId));

        return WendaUtil.getJSONString(ret ? 0 : 1, info);
    }

    @RequestMapping(value = {"/unfollowQuestion"}, method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String unfollowQuestion(@RequestParam("questionId") int questionId) {
        if(hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }

        Question q = questionService.getById(questionId);
        if(q == null) {
            return WendaUtil.getJSONString(1, "no such question.");
        }

        boolean ret = followService.unfollow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, questionId);

//        eventProducer.fireEvent(new EventModel(EventType.UNFOLLOW).setActorId(hostHolder.getUser().getId())
//                .setEntityType(EntityType.ENTITY_QUESTION).setEntityId(questionId)
//                .setEntityOwnerId(questionService.getById(questionId).getUserId()));


        Map<String, Object> info = new HashMap<>();
//        info.put("headUrl", hostHolder.getUser().getHeadUrl());
//        info.put("name", hostHolder.getUser().getName());
        info.put("id", hostHolder.getUser().getId());
        info.put("count", followService.getFollowerCount(EntityType.ENTITY_QUESTION, questionId));

        return WendaUtil.getJSONString(ret ? 0 : 1, info);
    }

    @RequestMapping(value = {"/user/{uid}/followers"}, method = {RequestMethod.GET})
    public String followers(Model model,
                            @PathVariable("uid") int uid) {

        List<Integer> followerIds = followService.getFollowers(EntityType.ENTITY_USER, uid, 0, 10);
        if(hostHolder.getUser() == null) {
            model.addAttribute("followers", getUseInfo(0, followerIds));
        }else {
            model.addAttribute("followers", getUseInfo(0, followerIds));
        }

        model.addAttribute("curUser", userService.getUser(uid));
        model.addAttribute("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, uid));

        return "followers";
    }

    @RequestMapping(value = {"/user/{uid}/followees"}, method = {RequestMethod.GET})
    public String followees(Model model,
                            @PathVariable("uid") int uid) {

        List<Integer> followeeIds = followService.getFollowees(uid, EntityType.ENTITY_USER, 0, 10);
        if(hostHolder.getUser() == null) {
            model.addAttribute("followees", getUseInfo(0, followeeIds));
        }else {
            model.addAttribute("followees", getUseInfo(hostHolder.getUser().getId(), followeeIds));
        }

        model.addAttribute("curUser", userService.getUser(uid));
        model.addAttribute("followeeCount", followService.getFolloweeCount(uid, EntityType.ENTITY_USER));

        return "followees";
    }




    private List<ViewObject> getUseInfo(int localUserId, List<Integer> userIds) {
        List<ViewObject> vos = new ArrayList<>();

        for (Integer userId : userIds) {
            User user = userService.getUser(userId);
            if(user == null) {
                continue;
            }

            ViewObject vo = new ViewObject();

            vo.set("user", user);
            vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
            vo.set("followeeCount", followService.getFolloweeCount(userId, EntityType.ENTITY_USER));
            vo.set("commentCount", commentService.getUserCommentCount(userId));
            if(localUserId == 0) {
                vo.set("followed", false);
            }else {
                vo.set("followed", followService.isFollower(localUserId, EntityType.ENTITY_USER, userId));
            }
            vos.add(vo);
        }
        return vos;
    }
}
