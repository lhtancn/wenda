package com.iip.controller;

import com.iip.async.EventModel;
import com.iip.async.EventProducer;
import com.iip.async.EventType;
import com.iip.model.Comment;
import com.iip.model.EntityType;
import com.iip.model.HostHolder;
import com.iip.service.CommentService;
import com.iip.service.LikeService;
import com.iip.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Demo on 4/14/2017.
 */
@Controller
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private CommentService commentService;

    @RequestMapping(value = {"/like"}, method = {RequestMethod.POST})
    @ResponseBody
    public String like(@RequestParam("commentId") int commentId) {
        if(hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }
        Comment comment = commentService.getCommentById(commentId);
        eventProducer.fireEvent(new EventModel(EventType.LIKE).setActorId(hostHolder.getUser().getId())
        .setEntityType(EntityType.ENTITY_COMMENT).setEntityId(commentId).setEntityOwnerId(comment.getUserId())
                .setExts("questionId", String.valueOf(comment.getEntityId())));

        long likeCount = likeService.like(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);
        return WendaUtil.getJSONString(0, String.valueOf(likeCount));
    }

    @RequestMapping(value = {"/dislike"}, method = {RequestMethod.POST})
    @ResponseBody
    public String disLike(@RequestParam("commentId") int commentId) {
        if(hostHolder.getUser() == null) {
            return WendaUtil.getJSONString(999);
        }
        long likeCount = likeService.disLike(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);
        return WendaUtil.getJSONString(0, String.valueOf(likeCount));
    }
}
