package com.kayn.recommender;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.kayn.client.EsClient;
import com.kayn.mapper.idf.CatCntMapper;
import com.kayn.mapper.idf.CatIdfMapper;
import com.kayn.mapper.idf.QueryCntMapper;
import com.kayn.mapper.idf.QueryIdfMapper;
import com.kayn.pojo.idf.CatCnt;
import com.kayn.pojo.idf.CatIdf;
import com.kayn.pojo.idf.QueryCnt;
import com.kayn.pojo.idf.QueryIdf;
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
    private QueryCntMapper queryCntMapper;

    @Resource
    private CatCntMapper catCntMapper;

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

                QueryCnt queryCnt = new QueryCnt().setUsername(username).setQuery(q).setCnt(cnt).setScore(score);
                QueryWrapper<QueryCnt> queryWrapper = new QueryWrapper<QueryCnt>().eq("username", username).eq("query", q);
                if (queryCntMapper.selectOne(queryWrapper) == null) {
                    queryCntMapper.insert(queryCnt);
                } else {
                    queryCntMapper.update(queryCnt, queryWrapper);
                }

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

                CatCnt catCnt = new CatCnt().setUsername(username).setCat(tag).setCnt(cnt).setScore(score);
                QueryWrapper<CatCnt> queryWrapper = new QueryWrapper<CatCnt>().eq("username", username).eq("cat", tag);
                if (catCntMapper.selectOne(queryWrapper) == null) {
                    catCntMapper.insert(catCnt);
                } else {
                    catCntMapper.update(catCnt, queryWrapper);
                }

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
}
