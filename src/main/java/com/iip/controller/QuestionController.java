package com.iip.controller;

import com.iip.async.EventModel;
import com.iip.async.EventProducer;
import com.iip.async.EventType;
import com.iip.model.*;
import com.iip.service.*;
import com.iip.util.JedisAdapter;
import com.iip.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Demo on 4/14/2017.
 */
@Controller
public class QuestionController {
    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    private QuestionService questionService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private FollowService followService;

    @Autowired
    private EventProducer eventProducer;


    @RequestMapping(value = {"/question/{questionId}"}, method = {RequestMethod.GET, RequestMethod.POST})
//    @ResponseBody
    public String detail(@PathVariable("questionId") int questionId,
                         Model model) {
        try{
            Question question = questionService.getById(questionId);
            List<Comment> commentList = commentService.getCommentByEntity(questionId, EntityType.ENTITY_QUESTION);
            List<ViewObject> comments = new ArrayList<>();



            model.addAttribute("question", question);
            for(Comment c : commentList) {
                ViewObject comment = new ViewObject();
                User user = userService.getUser(c.getUserId());
                comment.set("comment", c);
                comment.set("liked", likeService.getLikeStatus(user.getId(), EntityType.ENTITY_COMMENT, c.getId()));
                comment.set("likeCount", likeService.getLikeCount(EntityType.ENTITY_COMMENT, c.getId()));
                comment.set("user", user);
                comments.add(comment);
            }
            model.addAttribute("comments", comments);

            List<User> followUsers = new ArrayList<>();
            List<Integer> followUserIds = followService.getFollowers(EntityType.ENTITY_QUESTION, questionId, 10);
            for (Integer id : followUserIds) {
                followUsers.add(userService.getUser(id));
            }
            model.addAttribute("followUsers", followUsers);
            if(hostHolder.getUser() != null) {
                model.addAttribute("followed", followService.isFollower(hostHolder.getUser().getId(),
                        EntityType.ENTITY_QUESTION, questionId));
            }else {
                model.addAttribute("followed", false);
            }


        }catch (Exception e) {
            logger.error("failed." + e.getMessage());
        }
        return "detail";
    }

    @RequestMapping(value = {"/question/add"}, method = {RequestMethod.POST})
    @ResponseBody
    public String addQuestion(@RequestParam("title") String title,
                              @RequestParam("content") String content) {
        try{
            Question question = new Question();
            question.setTitle(title);
            question.setContent(content);
            question.setCommentCount(0);
            question.setCreatedDate(new Date());
            if(hostHolder.getUser() != null) {
                question.setUserId(hostHolder.getUser().getId());
            }else {
                question.setUserId(WendaUtil.ANONYMOUS_USERID);
            }


            if(questionService.addQuestion(question) > 0) {
                eventProducer.fireEvent(new EventModel(EventType.ADD_QUESTION).setEntityOwnerId(hostHolder.getUser().getId())
                        .setEntityId(question.getId()).setEntityType(EntityType.ENTITY_QUESTION).setActorId(question.getId())
                        .setExts("title", question.getTitle()).setExts("content", question.getContent()));
                return WendaUtil.getJSONString(0);
            }

        }catch (Exception e) {
            logger.error("add question failed. " + e.getMessage());

        }
        return WendaUtil.getJSONString(1, "failed");

    }
}
