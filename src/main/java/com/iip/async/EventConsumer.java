package com.iip.async;

import com.alibaba.fastjson.JSONObject;
import com.iip.util.JedisAdapter;
import com.iip.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Demo on 4/15/2017.
 */
@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware{
    @Autowired
    JedisAdapter jedisAdapter;

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    private ApplicationContext applicationContext;
    private Map<EventType, List<EventHandler>> config = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        if(beans != null) {
            for(Map.Entry<String, EventHandler> entry : beans.entrySet()) {
                List<EventType> types = entry.getValue().getSupportEventTypes();
                for(EventType type : types) {
                    if(!config.containsKey(type)) {
                        List<EventHandler> list = new ArrayList<>();
                        config.put(type, list);
                    }
                    config.get(type).add(entry.getValue());
                }
            }
        }

        Thread thread = new Thread(() ->{
                String key = RedisKeyUtil.getEvevtQueueKey();
                while(true) {
                    List<String> model = jedisAdapter.brpop(0, key);
                    for(String m : model) {
                        if(m.equals(key)) {
                            continue;
                        }
                        EventModel eventModel = JSONObject.parseObject(m, EventModel.class);
                        if(config.get(eventModel.getType()) == null) {
                            logger.error("wrong eventModel.");
                        }
                        for(EventHandler handler : config.get(eventModel.getType())) {
                            handler.doHandler(eventModel);
                        }
                    }
                }
        });
        thread.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
