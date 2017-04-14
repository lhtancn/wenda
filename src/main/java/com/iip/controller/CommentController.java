package com.iip.controller;

import com.iip.model.Comment;
import com.iip.model.EntityType;
import com.iip.model.HostHolder;
import com.iip.service.CommentService;
import com.iip.service.QuestionService;
import com.iip.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

/**
 * Created by Demo on 4/14/2017.
 */
@Controller
public class CommentController {
    public static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    CommentService commentService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    QuestionService questionService;

    @RequestMapping(value = {"/addComment"}, method = {RequestMethod.POST})
    public String addComment(@RequestParam("content") String content,
                             @RequestParam("questionId") int questionId) {
        try {
            Comment comment = new Comment();
            comment.setContent(content);
            comment.setCreatedDate(new Date());
            comment.setStatus(0);
            if(hostHolder.getUser() == null) {
                comment.setUserId(WendaUtil.ANONYMOUS_USERID);
            }else {
                comment.setUserId(hostHolder.getUser().getId());
            }
            comment.setEntityType(EntityType.ENTITY_QUESTION);
            comment.setEntityId(questionId);
            commentService.addCount(comment);

            int count = commentService.getCommentCount(questionId, EntityType.ENTITY_QUESTION);
            questionService.updateCommentCount(questionId, count);

        } catch(Exception e) {
            logger.error("add comment failed." + e.getMessage());
        }
        return "redirect:/question/" + questionId;
    }
}
