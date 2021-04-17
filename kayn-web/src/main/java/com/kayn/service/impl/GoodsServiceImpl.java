package com.kayn.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kayn.client.TaobaokeApiClient;
import com.kayn.pojo.good.Good;
import com.kayn.pojo.good.GoodDetail;
import com.kayn.pojo.good.GoodPage;
import com.kayn.pojo.good.PanelResult;
import com.kayn.result.Result;
import com.kayn.service.GoodsService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Value("${taobaoke.url}")
    private String url;

    @Value("${taobaoke.usertoken}")
    private String usertoken;

    @Value("${taobaoke.site_id}")
    private String site_id;

    @Value("${taobaoke.adzone_id}")
    private String adzone_id;

    @Resource
    private TaobaokeApiClient taobaokeApiClient;


    public Result<GoodPage> getGoodPage(String q, Integer pageSize, Integer pageNo, Integer sort, Integer priceGt, Integer priceLte) {
        Result<GoodPage> result = new Result<>();
        GoodPage goodPage = new GoodPage();

        HashMap<String, String> map = new HashMap<>();
        map.put("method", "taobao.tbk.sc.material.optional");
        map.put("usertoken", usertoken);
        map.put("site_id", site_id);
        map.put("adzone_id", adzone_id);
        map.put("q", q);
        map.put("page_size", String.valueOf(pageSize));
        map.put("page_no", String.valueOf(pageNo));
        if(sort != null) {
            if (sort == 1) {
                map.put("sort", "price_asc");
            } else if (sort == -1){
                map.put("sort", "price_des");
            }
        }
        if (priceGt != null) {
            map.put("end_price", String.valueOf(priceGt));
        }
        if (priceLte != null) {
            map.put("start_price", String.valueOf(priceLte));
        }

        String res = taobaokeApiClient.getData(url, map);
        try {
            // 获取返回结果
            JSONObject jsonObject = JSONObject.parseObject(res);
            // mapData
            JSONArray mapData = jsonObject.getJSONObject("result_list").getJSONArray("map_data");
            // 总条数
            Integer totalResults = jsonObject.getInteger("total_results");
            goodPage.setTotal(totalResults);

            // 保存数据到goodList中
            ArrayList<Good> goodList = new ArrayList<>();
            for (Object data : mapData) {
                JSONObject jsonGood = (JSONObject) data;
                Good good = new Good();
                good.setProductId(jsonGood.getLong("item_id"));
                good.setProductImageBig(jsonGood.getString("pict_url"));
                good.setProductName(jsonGood.getString("short_title"));
                good.setSubTitle(jsonGood.getString("nick"));
                good.setSalePrice(jsonGood.getDouble("zk_final_price"));
                goodList.add(good);
            }
            // goodResult结果集
            goodPage.setData(goodList);

            // result
            result.setSuccess(true);
            result.setCode(200);
            result.setResult(goodPage);
            result.setMessage("success");
            result.setTimestamp(new Date().getTime());
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
            result.setCode(500);
            result.setResult(null);
            result.setMessage("fail");
            result.setTimestamp(new Date().getTime());
        }

        return result;
    }

    @Override
    public Result<GoodDetail> getGoodDetail(Long productId) {
        Result<GoodDetail> result = new Result<>();
        GoodDetail goodDetail = new GoodDetail();

        HashMap<String, String> map = new HashMap<>();
        map.put("method", "taobao.tbk.item.info.get");
        map.put("usertoken", usertoken);
        map.put("num_iids", String.valueOf(productId));

        String res = taobaokeApiClient.getData(url, map);

        try {
            // 获取返回结果
            JSONObject jsonObject = JSONObject.parseObject(res);
            JSONObject jsonGood = jsonObject.getJSONObject("results").getJSONObject("n_tbk_item");
            goodDetail.setProductId(jsonGood.getLong("num_iid"));
            goodDetail.setProductName(jsonGood.getString("title"));
            goodDetail.setSubTitle(jsonGood.getString("nick"));
            goodDetail.setSalePrice(jsonGood.getDouble("zk_final_price"));
            goodDetail.setCatName(jsonGood.getString("cat_name"));
            goodDetail.setLimitNum(10);
            try {
                Document doc = Jsoup.connect(jsonGood.getString("item_url")).get();
                goodDetail.setDetail(doc.body().getElementById("J_MainWrap").html());
            } catch (Exception e) {
                goodDetail.setDetail(null);
            }

            goodDetail.setProductImageBig(jsonGood.getString("pict_url"));

            JSONArray imageSmallArray = jsonGood.getJSONObject("small_images").getJSONArray("string");
            ArrayList<String> imageSmallList = new ArrayList<>();
            for (Object imageSmall : imageSmallArray) {
                imageSmallList.add((String) imageSmall);
            }
            goodDetail.setProductImageSmall(imageSmallList);

            // result
            result.setSuccess(true);
            result.setCode(200);
            result.setResult(goodDetail);
            result.setMessage("success");
            result.setTimestamp(new Date().getTime());

        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
            result.setCode(500);
            result.setResult(null);
            result.setMessage("fail");
            result.setTimestamp(new Date().getTime());
        }
        return result;
    }

    @Override
    public Result<PanelResult> getRecommend(Long productId) {

        return null;
    }
}
