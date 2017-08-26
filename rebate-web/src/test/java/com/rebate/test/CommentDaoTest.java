package com.rebate.test;


import com.rebate.common.util.HttpClientUtil;
import com.rebate.common.util.JsonUtil;
import com.rebate.dao.CommentDao;
import com.rebate.dao.ProductDao;
import com.rebate.domain.Comment;
import com.rebate.domain.Product;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@ContextConfiguration(locations = {"/spring-config.xml"})
public class CommentDaoTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private CommentDao commentDao;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Test
    public void batchInsert() {
        for (long skuId = 1001669; skuId < 10000000000l; skuId++) {
            try{

                List<Comment> list = grabComments(skuId);
                for (Comment comment : list) {
                    insert(comment);
                }
            }catch (Exception e){
                 System.out.println("skuId:"+skuId+",errorMsg:"+e.getMessage());
            }
        }
    }

    public void insert(Comment comment) {

        Comment existsComment = commentDao.findById(comment);
        if (null == existsComment) {
            commentDao.insert(comment);
        }
    }

    public List<Comment> grabComments(Long skuId) {
        List<Comment> commentList = new ArrayList<Comment>();
        for (int page = 0; page < 100; page++) {
            String url = "https://club.jd.com/comment/skuProductPageComments.action?productId=" + skuId + "&score=0&sortType=5&page=" + page + "&pageSize=10&isShadowSku=0&fold=1";
            String json = HttpClientUtil.get(url);
            if(!json.contains("productCommentSummary")){
                return commentList;
            }
            Map rootMap = JsonUtil.fromJson(json, Map.class);
            if (null == rootMap) {
                return commentList;
            }
            Map summary = (Map) rootMap.get("productCommentSummary");
            if (null == summary) {
                return commentList;
            }

            Integer commentCount = Integer.parseInt(summary.get("commentCount").toString());
            if (null != commentCount && commentCount > 0) {
                List<Map> list = (List) rootMap.get("comments");
                if (null != list) {
                    for (Map map : list) {
                        try{

                            Comment comment = new Comment();
                            comment.setProductId(Long.parseLong(map.get("referenceId").toString()));
                            comment.setCommentId(Long.parseLong(map.get("id").toString()));
                            comment.setStatus(1);
                            comment.setCreated(sdf.parse(map.get("creationTime").toString()));
                            comment.setNickname(map.get("nickname").toString());
                            comment.setGuid(map.get("guid").toString());
                            comment.setContent(map.get("content").toString());
                            commentList.add(comment);
                        }catch (Exception e){
                             System.out.println("skuId:"+skuId+",error:"+e.getMessage());
                        }
                    }
                }
            }
        }

        return commentList;
    }

}
