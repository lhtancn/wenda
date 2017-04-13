package com.iip.service;

import com.iip.dao.UserDAO;
import com.iip.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Demo on 4/13/2017.
 */
@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserDAO userDAO;

    public User selectByName(String name){
        return userDAO.selectByName(name);
    }

    public User getUser(int id){
        return userDAO.selectById(id);
    }

}
