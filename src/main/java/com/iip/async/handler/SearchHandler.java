package com.iip.async.handler;

import com.iip.async.EventHandler;
import com.iip.async.EventModel;
import com.iip.async.EventType;
import com.iip.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Demo on 4/17/2017.
 */
@Component
public class SearchHandler implements EventHandler{
    private static final Logger logger = LoggerFactory.getLogger(SearchHandler.class);

    @Autowired
    private SearchService searchService;

    @Override
    public void doHandler(EventModel eventModel) {
        try{
            boolean tag = searchService.indexQuestion(eventModel.getEntityId(), eventModel.getExts("title"),
                    eventModel.getExts("content"));
            if(!tag) {
                logger.error("add question failed.");
            }

        }catch (Exception e) {
            logger.error("add question failed." + e.getMessage());
        }
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.ADD_QUESTION);
    }
}
