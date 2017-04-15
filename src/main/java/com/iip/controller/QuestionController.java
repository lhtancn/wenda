package com.iip.controller;

import com.iip.model.*;
import com.iip.service.CommentService;
import com.iip.service.LikeService;
import com.iip.service.QuestionService;
import com.iip.service.UserService;
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
    UserService userService;

    @Autowired
    LikeService likeService;


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
                return WendaUtil.getJSONString(0);
            }

        }catch (Exception e) {
            logger.error("add question failed. " + e.getMessage());

        }
        return WendaUtil.getJSONString(1, "failed");

    }
}
