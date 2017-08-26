package com.rebate.dao.impl;

import com.rebate.common.dao.BaseDao;
import com.rebate.dao.CommentDao;
import com.rebate.domain.Comment;

/**
 * Created by weishi on 2017/7/15.
 */
public class CommentDaoImpl extends BaseDao implements CommentDao {
    @Override
    public long insert(Comment comment) {
        return (Long)insert("Comment.insert",comment);
    }

    @Override
    public Comment findById(Comment comment) {
        return (Comment) queryForObject("Comment.findById",comment);
    }
}
