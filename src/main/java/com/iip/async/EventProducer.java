package com.iip.async;

import com.alibaba.fastjson.JSONObject;
import com.iip.util.JedisAdapter;
import com.iip.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Demo on 4/15/2017.
 */
@Service
public class EventProducer {
    @Autowired
    private JedisAdapter jedisAdapter;

    public boolean fireEvent(EventModel eventModel) {
        try{
            String json = JSONObject.toJSONString(eventModel);
            String key = RedisKeyUtil.getEvevtQueueKey();
            jedisAdapter.lpush(key, json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
