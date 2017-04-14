package com.iip.service;

import com.iip.dao.LoginTicketDAO;
import com.iip.dao.UserDAO;
import com.iip.model.LoginTicket;
import com.iip.model.User;
import com.iip.util.WendaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import org.apache.commons.lang.StringUtils;

/**
 * Created by Demo on 4/13/2017.
 */
@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LoginTicketDAO loginTicketDAO;

    public User selectByName(String name){
        return userDAO.selectByName(name);
    }

    public User getUser(int id){
        return userDAO.selectById(id);
    }

    public Map<String, Object> register(String username, String password) {
        Map<String, Object> map = new HashMap<>();

        if(StringUtils.isBlank(username)) {
            map.put("msg", "username mustn't be null.");
            return map;
        }
        if(StringUtils.isBlank(password)) {
            map.put("msg", "password mustn't be null.");
            return map;
        }

        User user = userDAO.selectByName(username);
        if(user != null) {
            map.put("msg", "used username.");
            return map;
        }

        user = new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0, 5));
        user.setPassword(WendaUtil.MD5(password + user.getSalt()));
        String head = String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000));
        user.setHeadUrl(head);
        userDAO.addUser(user);

        //login
        String ticket = addTicket(user.getId());
        map.put("ticket", ticket);
        return map;
    }


    public Map<String, Object> login(String username, String password) {
        Map<String, Object> map = new HashMap<>();

        if(StringUtils.isBlank(username)) {
            map.put("msg", "username mustn't be null.");
            return map;
        }
        if(StringUtils.isBlank(password)) {
            map.put("msg", "password mustn't be null.");
            return map;
        }

        User user = userDAO.selectByName(username);
        if(user == null) {
            map.put("msg", "user is not exist.");
            return map;
        }

        if(!WendaUtil.MD5(password + user.getSalt()).equals(user.getPassword())){
            map.put("msg", "password is not correct.");
            return map;
        }

        //login
        String ticket = addTicket(user.getId());
        map.put("ticket", ticket);
        return map;
    }

    public void logout(String ticket) {
        loginTicketDAO.updateStatus(ticket, 1);
    }


    private String addTicket(int userId) {
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(userId);
        ticket.setStatus(0);
        ticket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
        Date date = new Date();
        date.setTime(date.getTime() + 1000 * 3600 * 24);
        ticket.setExpired(date);
        loginTicketDAO.addTicket(ticket);
        return ticket.getTicket();
    }

}
