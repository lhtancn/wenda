package com.iip.controller;

import com.iip.model.EntityType;
import com.iip.model.Feed;
import com.iip.model.HostHolder;
import com.iip.service.FeedService;
import com.iip.service.FollowService;
import com.iip.util.JedisAdapter;
import com.iip.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Demo on 4/16/2017.
 */
@Controller
public class FeedController {
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private FeedService feedService;

    @Autowired
    private JedisAdapter jedisAdapter;

    @Autowired
    private FollowService followService;

    @RequestMapping(value = {"/pushfeeds"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String getPushFeeds(Model model) {
        int localUserId = hostHolder.getUser() == null ? 0 : hostHolder.getUser().getId();
        String timeLineKey = RedisKeyUtil.getTimeLineKey(localUserId);
        List<String> feedIds = jedisAdapter.lrange(timeLineKey, 0, 10);
        List<Feed> list = new ArrayList<>();
        for(String id : feedIds) {
            list.add(feedService.getById(Integer.parseInt(id)));
        }
        model.addAttribute("feeds", list);
        return "feeds";
    }

    @RequestMapping(value = {"/pullfeeds"}, method = {RequestMethod.POST, RequestMethod.GET})
    public String getPullFeeds(Model model) {
        int localUserId = hostHolder.getUser() == null ? 0 : hostHolder.getUser().getId();
        List<Integer> followees = new ArrayList<>();
        if(localUserId != 0) {
            followees = followService.getFollowees(localUserId, EntityType.ENTITY_USER, Integer.MAX_VALUE);
        }
        List<Feed> feeds = feedService.getUserFeeds(Integer.MAX_VALUE, followees, 10);
        model.addAttribute("feeds", feeds);
        return "feeds";
    }
}
