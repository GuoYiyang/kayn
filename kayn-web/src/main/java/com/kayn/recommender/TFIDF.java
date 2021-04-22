package com.kayn.recommender;

import com.alibaba.fastjson.JSONObject;
import com.kayn.client.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;

@Service
public class TFIDF {
    private static final Double TAOBAO_ALL_GOOD_CNT = 800000000D;

    @Value("${taobaoke.url}")
    private String url;
    @Value("${taobaoke.usertoken}")
    private String usertoken;
    @Value("${taobaoke.site_id}")
    private String site_id;
    @Value("${taobaoke.adzone_id}")
    private String adzone_id;

    @Resource
    private HttpClient taobaokeApiClient;

    public Long getTaobaoGoodCnt(String key) {
        HashMap<String, String> map = new HashMap<>();
        map.put("method", "taobao.tbk.sc.material.optional");
        map.put("usertoken", usertoken);
        map.put("site_id", site_id);
        map.put("adzone_id", adzone_id);
        map.put("q", key);

        Long totalResults = null;
        try {
            String res = taobaokeApiClient.getData(url, map);
            // 获取返回结果
            JSONObject jsonObject = JSONObject.parseObject(res);
            // 总条数
            totalResults = jsonObject.getLong("total_results");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return totalResults;
    }

    public Double getTFIDF(String key, Integer cnt) {
        double res = 0D;
        Long totalCnt = getTaobaoGoodCnt(key);
        if (totalCnt != null) {
            Double IDF = Math.log10(TAOBAO_ALL_GOOD_CNT / (totalCnt + 1 ));
            res = cnt * IDF;
        }
        return res;
    }

}
