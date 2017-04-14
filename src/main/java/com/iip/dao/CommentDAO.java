package com.iip.dao;

import com.iip.model.Comment;
import com.iip.model.Question;
import org.apache.ibatis.annotations.*;

import java.awt.*;
import java.util.List;

/**
 * Created by Demo on 4/13/2017.
 */
@Mapper
public interface CommentDAO {
    String TABLE_NAME = "comment";
    String INSERT_FIELDS = " content, created_date, user_id, entity_type, entity_id, status ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
    ") Values (#{content}, #{createdDate}, #{userId}, #{entityType}, #{entityId}, #{status})"})
    int addComment(Comment comment);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " " +
            "where entity_id = #{entityId} and entity_type = #{entityType} order by created_date desc"})
    List<Comment> selectCommentByEntity(@Param("entityId") int entityId, @Param("entityType") int entityType);

    @Select({"select count(id) from ", TABLE_NAME, " " +
            "where entity_id = #{entityId} and entity_type = #{entityType}"})
    int getCommentCount(@Param("entityId") int entityId, @Param("entityType") int entityType);

    @Select({"select count(id) from ", TABLE_NAME, " " +
            "where user_id = #{userId}"})
    int getUserCommentCount(@Param("userId") int userId);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id = #{id}"})
    Comment getCommentById(int id);

    @Update({"update ", TABLE_NAME, " set status = #{status} where id = #{id}"})
//    int updateCommentCount(int id, int commentCount);          //两种获参方法
    int updateStatus(@Param("id") int id, @Param("status") int status);

}
