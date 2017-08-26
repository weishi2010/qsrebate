package com.rebate.dao;

import com.rebate.domain.Comment;

/**
 * Created by weishi on 2017/7/15.
 */
public interface CommentDao {
    /**
     * 插入
     * @param comment
     * @return
     */
    long insert(Comment comment);

    /**
     * 根据id查询
     * @param comment
     * @return
     */
    Comment findById(Comment comment);
}
