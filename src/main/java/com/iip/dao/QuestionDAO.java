package com.iip.dao;

import com.iip.model.Question;
import com.iip.model.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by Demo on 4/13/2017.
 */
@Mapper
public interface QuestionDAO {
    String TABLE_NAME = "question";
    String INSERT_FIELDS = " title, content, user_id, created_date, comment_count ";
    String SELECT_FIELDS = " id " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
    "), Values (#{title}, #{content}, #{userId}, #{createdDate}, #{commentCount})"})
    int addQuestion(Question question);

    List<Question> selectLatestQuestions(@Param("userId") int userId, @Param("offset") int offset,
                                         @Param("limit") int limit);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id = #{id}"})
    Question getById(int id);

    @Update({"update ", TABLE_NAME, " set comment_count = #{commentCount} where id = #{id}"})
//    int updateCommentCount(int id, int commentCount);          //两种获参方法
    int updateCommentCount(@Param("id") int id, @Param("commentCount") int CommentCount);

}
