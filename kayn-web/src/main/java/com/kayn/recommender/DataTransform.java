package com.kayn.recommender;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.kayn.client.EsClient;
import com.kayn.mapper.idf.CatIdfMapper;
import com.kayn.mapper.idf.QueryIdfMapper;
import com.kayn.mapper.rfm.UserRFMMapper;
import com.kayn.pojo.idf.CatIdf;
import com.kayn.pojo.idf.QueryIdf;
import com.kayn.pojo.rfm.UserRFM;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class DataTransform {

    @Resource
    private TFIDF tfidf;

    @Resource
    private QueryIdfMapper queryIdfMapper;

    @Resource
    private CatIdfMapper catIdfMapper;

    @Resource
    private UserRFMMapper userRFMMapper;

    @Resource
    private RFM rfm;

    public String getPreferQuery(String username) {
        String res = null;
        Map<String, Object> map = EsClient.getData("user_query_cnt", username);

        if (map != null) {
            List<Map<String, Object>> queryList = (List<Map<String, Object>>) map.get("query");
            Double maxScore = 0D;
            for (Map<String, Object> query : queryList) {
                String q = query.get("query").toString();
                // 排除默认查询
                if ("默认".equals(q)) {
                    continue;
                }
                Integer cnt = Integer.parseInt(query.get("cnt").toString());
                // 获取TF-IDF推荐值
                Double score = tfidf.getTFIDF(q, cnt);
                if (score > maxScore) {
                    maxScore = score;
                    res = q;
                }
            }
            // 保存到数据库中
            QueryIdf queryIdf = new QueryIdf().setUsername(username).setQuery(res).setScore(maxScore);
            if (queryIdfMapper.selectOne(new QueryWrapper<QueryIdf>().eq("username", username)) != null) {
                queryIdfMapper.update(queryIdf, new UpdateWrapper<QueryIdf>().eq("username", username));
            } else {
                queryIdfMapper.insert(queryIdf);
            }
        }
        return res;
    }

    public String getPreferCat(String username) {
        String res = null;
        Map<String, Object> map = EsClient.getData("user_cat_cnt", username);

        if (map != null) {
            List<Map<String, Object>> catList = (List<Map<String, Object>>) map.get("cat");
            Double maxScore = 0D;
            for (Map<String, Object> cat : catList) {
                String tag = cat.get("cat").toString();
                Integer cnt = Integer.parseInt(cat.get("cnt").toString());
                // 获取TF-IDF推荐值
                Double score = tfidf.getTFIDF(tag, cnt);
                if (score > maxScore) {
                    maxScore = score;
                    res = tag;
                }
            }
            // 保存到数据库中
            CatIdf catIdf = new CatIdf().setUsername(username).setCat(res).setScore(maxScore);
            if (catIdfMapper.selectOne(new QueryWrapper<CatIdf>().eq("username", username)) != null) {
                catIdfMapper.update(catIdf, new UpdateWrapper<CatIdf>().eq("username", username));
            } else {
                catIdfMapper.insert(catIdf);
            }
        }
        return res;
    }

    public Integer getUserRFM(String username) {
        Integer res = null;
        Map<String, Object> map = EsClient.getData("user_pay_order", username);

        if (map != null) {
            Long lastTime  = (Long) map.get("lastTime");
            Integer cnt = (Integer) map.get("cnt");
            Double orderTotal = (Double) map.get("orderTotal");

            Integer rfm = this.rfm.getRFM(lastTime, cnt, orderTotal);
            res = rfm;

            // 保存到数据库
            UserRFM userRFM = new UserRFM()
                    .setUsername(username)
                    .setRecency(lastTime)
                    .setFrequency(cnt)
                    .setMonetary(orderTotal)
                    .setRfm(rfm);
            if (userRFMMapper.selectOne(new QueryWrapper<UserRFM>().eq("username", username)) != null) {
                userRFMMapper.update(userRFM, new UpdateWrapper<UserRFM>().eq("username", username));
            } else {
                userRFMMapper.insert(userRFM);
            }
        }
        return res;
    }

}
