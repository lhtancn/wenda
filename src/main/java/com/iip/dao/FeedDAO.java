package com.iip.dao;

import com.iip.model.Comment;
import com.iip.model.Feed;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Created by Demo on 4/13/2017.
 */
@Mapper
public interface FeedDAO {
    String TABLE_NAME = " feed ";
    String INSERT_FIELDS = " user_id, created_date, data, type ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
    ") Values (#{userId}, #{createdDate}, #{data}, #{type})"})
    int addFeed(Feed feed);


    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id = #{id}"})
    Feed getFeedById(int id);

    List<Feed> selectUserFeeds(@Param("maxId") int maxId, @Param("userIds") List<Integer> userIds, @Param("count") int count);

}
