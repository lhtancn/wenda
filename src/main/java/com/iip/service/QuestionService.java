package com.iip.service;

import com.iip.dao.QuestionDAO;
import com.iip.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Demo on 4/13/2017.
 */
@Service
public class QuestionService {
    @Autowired
    private QuestionDAO questionDAO;

    public Question getById(int id) {
        return questionDAO.getById(id);
    }

    public int addQuestion(Question question) {
        return questionDAO.addQuestion(question) > 0 ? question.getId() : 0;
    }
    
    public List<Question> getLatestQuestions(int userId, int offset, int limit) {
        return questionDAO.selectLatestQuestions(userId, offset, limit);
    }

    public int updateCommentCount(int id, int count) {
        return questionDAO.updateCommentCount(id, count);
    }
}
