package com.iip.dao;

import com.iip.model.Message;
import com.iip.model.Question;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by Demo on 4/13/2017.
 */
@Mapper
public interface MessageDAO {
    String TABLE_NAME = "message";
    String INSERT_FIELDS = " from_id, to_id, content, created_date, has_read, conversation_id ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, " (", INSERT_FIELDS,
    ") Values (#{fromId}, #{toId}, #{content}, #{createdDate}, #{hasRead}, #{conversationId})"})
    int addMessage(Message message);

    @Select({"select " + INSERT_FIELDS + ", count(id) as id from ( select * from " + TABLE_NAME + " where from_id = #{userId} " +
            " or to_id = #{userId} order by created_date desc) tt group by " +
            " conversation_id order by created_date desc limit #{offset}, #{limit}"})
    List<Message> getConversationList(@Param("userId") int userId, @Param("offset") int offset, @Param("limit") int limit);

    @Select({"select " + SELECT_FIELDS + " from ", TABLE_NAME, " where conversation_id = #{conversationId}" +
            " order by created_date desc limit #{offset}, #{limit}"})
    List<Message> getConversationDetail(@Param("conversationId") String conversationId, @Param("offset") int offset, @Param("limit") int limit);


    @Select({"select count(id) from ", TABLE_NAME, " where has_read = 0 and to_id = #{userId}" +
            " and conversation_id = #{conversationId}"})
    int getConversationUnreadCount(@Param("userId") int userId, @Param("conversationId") String conversationId);

    @Update({"update ", TABLE_NAME, " set has_read = #{hasRead} where id = #{id}"})
    int updateHasRead(@Param("id") int id, @Param("hasRead") int hasRead);

}
