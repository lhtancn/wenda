package com.iip.controller;

import com.iip.model.*;
import com.iip.service.CommentService;
import com.iip.service.FollowService;
import com.iip.service.QuestionService;
import com.iip.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Demo on 4/13/2017.
 */
@Controller
public class IndexController {
    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private FollowService followService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    private List<ViewObject> getQuestions(int userId, int offset, int limit) {
        List<ViewObject> vos = new ArrayList<>();
        List<Question> list = questionService.getLatestQuestions(userId, offset, limit);

        for(Question q : list) {
            ViewObject vo = new ViewObject();
            vo.set("question", q);
            vo.set("user", userService.getUser(q.getUserId()));
            vo.set("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, q.getId()));
//            vo.set("followCount");
            vos.add(vo);
        }
        return vos;
    }

    @RequestMapping(value = {"/", "/index"}, method = {RequestMethod.POST, RequestMethod.GET})
    public String index(Model model) {
        model.addAttribute("vos", getQuestions(0, 0, 10));
        return "index";
    }

    @RequestMapping(value = {"/user/{userId}"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String userIndex(Model model,
                            @PathVariable("userId") int userId) {
        model.addAttribute("vos", getQuestions(userId, 0, 10));

        User user = userService.getUser(userId);
        ViewObject vo = new ViewObject();
        vo.set("user", user);
        vo.set("commentCount", commentService.getUserCommentCount(userId));
        vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
        vo.set("followeeCount", followService.getFolloweeCount(userId, EntityType.ENTITY_USER));
//        vo.set("fo");

        if(hostHolder.getUser() != null) {
            vo.set("followed", followService.isFollower(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId));
        }else {
            vo.set("followed", false);
        }
        model.addAttribute("profileUser", vo);
        return "profile";
    }

}
