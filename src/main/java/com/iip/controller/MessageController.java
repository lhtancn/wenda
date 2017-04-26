package com.iip.controller;

import com.iip.model.HostHolder;
import com.iip.model.Message;
import com.iip.model.User;
import com.iip.model.ViewObject;
import com.iip.service.MessageService;
import com.iip.service.UserService;
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

/**
 * Created by Demo on 4/14/2017.
 */
@Controller
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(value = {"/msg/list"}, method = {RequestMethod.GET})
    public String conversationList(Model model) {
        try {
            User user = hostHolder.getUser();
            if(user == null) {
                return "redirect:/reglogin";
            }
            int localUserId = user.getId();
            List<Message> messageList = messageService.getConversationList(localUserId, 0, 10);
            List<ViewObject> vos = new ArrayList<>();
            for(Message message : messageList) {
                ViewObject vo = new ViewObject();
                vo.set("message", message);
                int unread = messageService.getConversationUnreadCount(localUserId, message.getConversationId());
                vo.set("unread", unread);

                if(message.getFromId() == localUserId) {
                    vo.set("user", userService.getUser(message.getToId()));
                }else {
                    vo.set("user", userService.getUser(message.getFromId()));
                }
                vos.add(vo);
            }
            model.addAttribute("conversations", vos);
        }catch (Exception e) {
            logger.error("msgList failed." + e.getMessage());
        }

        return "letter";

    }

    @RequestMapping(value = {"/msg/detail"}, method = {RequestMethod.GET})
    public String conversationDetail(Model model,
                                     @RequestParam("conversationId") String conversationId) {
       try {
           if(hostHolder.getUser() == null) {
               return "redirect:/reglogin";
           }
           List<Message> messageList = messageService.getConversationDetail(conversationId, 0, 10);
           List<ViewObject> vos = new ArrayList<>();
           for(Message m : messageList) {
               ViewObject vo = new ViewObject();
               vo.set("message", m);
               int fromId = m.getFromId();
               vo.set("headUrl", userService.getUser(fromId).getHeadUrl());
               vos.add(vo);
           }
           model.addAttribute("messages", vos);
       }catch (Exception e) {
           logger.error("wrong conversation detail." + e.getMessage());
       }
        return "letterDetail";
    }


    @RequestMapping(value = {"/msg/addMessage"}, method = {RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("toName") String toName,
                             @RequestParam("content") String content) {
        try{
            Message message = new Message();
            message.setContent(content);
            User user = userService.selectByName(toName);
            if(user == null) {
                return WendaUtil.getJSONString(1, "no exist user");
            }
            int toId = user.getId();
            int fromId;
            if(hostHolder.getUser() == null) {
                return WendaUtil.getJSONString(999, "need login.");
            }else {
                fromId = hostHolder.getUser().getId();
            }
            message.setFromId(fromId);
            message.setToId(toId);
            message.setCreatedDate(new Date());
            String conversationId = fromId < toId ? fromId + "_" + toId : toId + "_" + fromId;
            message.setConversationId(conversationId);
            message.setHasRead(0);
            messageService.addMessage(message);
            return WendaUtil.getJSONString(0);
        } catch (Exception e) {
            logger.error("send message failed." + e.getMessage());
            return WendaUtil.getJSONString(1, "send message failed.");
        }
    }
}
