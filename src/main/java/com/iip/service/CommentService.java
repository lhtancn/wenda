package com.iip.service;

import com.iip.dao.CommentDAO;
import com.iip.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Demo on 4/14/2017.
 */
@Service
public class CommentService {
    @Autowired
    CommentDAO commentDAO;

    @Autowired
    SensitiveService sensitiveService;

    public List<Comment> getCommentByEntity(int entityId, int entityType) {
        return commentDAO.selectCommentByEntity(entityId, entityType);
    }

    public int getCommentCount(int entityId, int entityType) {
        return commentDAO.getCommentCount(entityId, entityType);
    }

    public int getUserCommentCount(int userId) {
        return commentDAO.getUserCommentCount(userId);
    }

    public int addCount(Comment comment) {
        comment.setContent(sensitiveService.filter(comment.getContent()));
        return (commentDAO.addComment(comment) > 0 ? comment.getId() : 0);
    }

    public Comment getCommentById(int id) {
        return commentDAO.getCommentById(id);
    }
}
