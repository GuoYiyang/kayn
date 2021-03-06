package com.kayn.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kayn.client.HttpClient;
import com.kayn.mapper.slide.SlideMapper;
import com.kayn.mapper.user.UserRFMMapper;
import com.kayn.pojo.good.*;
import com.kayn.pojo.slide.Slide;
import com.kayn.pojo.user.UserRFM;
import com.kayn.recommender.DataTransform;
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
import java.util.List;

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
    private HttpClient taobaokeApiClient;

    @Resource
    private DataTransform dataTransform;

    @Resource
    private UserRFMMapper userRFMMapper;

    @Resource
    private SlideMapper slideMapper;


    public Result<GoodPage> getGoodPage(String q, Integer pageSize, Integer pageNo, Integer sort, Double priceGt, Double priceLte) {
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
        } else {
            map.put("sort", "total_sales_des");
        }
        if (priceGt != null) {
            map.put("end_price", String.valueOf(priceGt));
        }
        if (priceLte != null) {
            map.put("start_price", String.valueOf(priceLte));
        }

        String res = taobaokeApiClient.getData(url, map);
        try {
            // ??????????????????
            JSONObject jsonObject = JSONObject.parseObject(res);
            // mapData
            JSONArray mapData = jsonObject.getJSONObject("result_list").getJSONArray("map_data");
            // ?????????
            Integer totalResults = jsonObject.getInteger("total_results");
            goodPage.setTotal(totalResults);

            // ???????????????goodList???
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
            // goodResult?????????
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
            // ??????????????????
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
    public Result<List<PanelResult>> getGoodHome(String username) {
        Result<List<PanelResult>> result = new Result<>();
        List<PanelResult> resultList = new ArrayList<>();
        String preferQuery = dataTransform.getPreferQuery(username);
        String preferCat = dataTransform.getPreferCat(username);
        String rfm = userRFMMapper.selectOne(new QueryWrapper<UserRFM>().eq("username", username)).getRfm();

        try {
            int id = 0, type = 0, sortOrder = 0, position = 0, limitNum = 8, status = 1;
            PanelResult pr = new PanelResult();
            List<PanelContents> pcs = new ArrayList<>();
            if (rfm != null) {
                Slide slide = slideMapper.selectOne(new QueryWrapper<Slide>().eq("rfm", rfm));
                PanelContents pc1 = new PanelContents()
                        .setType(0)
                        .setPanelId(7)
                        .setSortOrder(1)
                        .setFullUrl(slide.getUrl())
                        .setPicUrl(slide.getPic())
                        .setProductImageBig(slide.getPic());
                pcs.add(pc1);
            }
            pr.setId(7)
                    .setName("?????????")
                    .setType(type ++)
                    .setSortOrder(sortOrder ++)
                    .setPosition(position)
                    .setLimitNum(limitNum)
                    .setStatus(status)
                    .setPanelContents(pcs);
            resultList.add(pr);


            // ???????????????????????????
            PanelResult panelResult = new PanelResult();
            List<PanelContents> panelContentsList;
            if (preferQuery != null) {
                panelResult.setName("?????????????????????" + preferQuery);
                panelContentsList = getPanelContents(preferQuery, 4);
            } else {
                panelResult.setName("???????????????????????????");
                panelContentsList = getPanelContents("??????", 4);
            }

            panelResult.setId(id ++)
                    .setType(type ++)
                    .setSortOrder(sortOrder ++)
                    .setPosition(position)
                    .setLimitNum(limitNum)
                    .setStatus(status)
                    .setPanelContents(panelContentsList);
            resultList.add(panelResult);

            if (preferCat != null) {
                // ??????????????????
                String[] split = preferCat.split("/");
                for (String s : split) {
                    List<PanelContents> panelContents = getPanelContents(s, 2);
                    PanelResult panelRes = new PanelResult();
                    panelRes.setName("???????????????????????????" + s)
                            .setId(id ++)
                            .setType(type)
                            .setSortOrder(sortOrder ++)
                            .setPosition(position)
                            .setLimitNum(limitNum)
                            .setStatus(status)
                            .setPanelContents(panelContents);
                    resultList.add(panelRes);
                }
            } else {
                List<PanelContents> panelContents = getPanelContents("??????", 2);
                PanelResult panelRes = new PanelResult();
                panelRes.setName("?????????????????????????????????")
                        .setId(id)
                        .setType(type)
                        .setSortOrder(sortOrder)
                        .setPosition(position)
                        .setLimitNum(limitNum)
                        .setStatus(status)
                        .setPanelContents(panelContents);
                resultList.add(panelRes);
            }

            result.setSuccess(true)
                    .setCode(200)
                    .setMessage("????????????????????????")
                    .setResult(resultList)
                    .setTimestamp(System.currentTimeMillis());

        } catch (Exception e) {
           e.printStackTrace();
            result.setSuccess(false)
                    .setCode(500)
                    .setMessage("????????????????????????")
                    .setTimestamp(System.currentTimeMillis());
        }

        return result;
    }

    @Override
    public Result<List<PanelResult>> getRecommend(String username) {
        Result<List<PanelResult>> result = new Result<>();
        List<PanelResult> resultList = new ArrayList<>();

        String preferQuery = dataTransform.getPreferQuery(username);
        PanelResult panelResult = new PanelResult();

        try {
            List<PanelContents> panelContents;
            if (preferQuery != null) {
                panelContents = getPanelContents(preferQuery, 2);
                panelResult.setName("???????????????" + preferQuery);
            } else {
                panelContents = getPanelContents("??????", 2);
                panelResult.setName("?????????????????????");
            }

            panelResult.setType(2)
                    .setSortOrder(6)
                    .setPosition(0)
                    .setLimitNum(2)
                    .setStatus(1)
                    .setPanelContents(panelContents);
            resultList.add(panelResult);

            result.setSuccess(true)
                    .setCode(200)
                    .setMessage("????????????????????????")
                    .setResult(resultList)
                    .setTimestamp(System.currentTimeMillis());

        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false)
                    .setCode(500)
                    .setMessage("????????????????????????")
                    .setTimestamp(System.currentTimeMillis());
        }

        return result;
    }

    public List<PanelContents> getPanelContents(String q, Integer pageSize) throws Exception {
        HashMap<String, String> map = new HashMap<>();
        map.put("method", "taobao.tbk.sc.material.optional");
        map.put("usertoken", usertoken);
        map.put("site_id", site_id);
        map.put("adzone_id", adzone_id);
        map.put("sort", "total_sales_des");
        map.put("q", q);
        map.put("page_size", String.valueOf(pageSize));
        map.put("page_no", String.valueOf(1));

        String res = taobaokeApiClient.getData(url, map);

        try {
            // ??????????????????
            JSONObject jsonObject = JSONObject.parseObject(res);
            // mapData
            JSONArray mapData = jsonObject.getJSONObject("result_list").getJSONArray("map_data");

            int sortOrder = 0;
            List<PanelContents> panelContentsList = new ArrayList<>();

            for (Object data : mapData) {
                JSONObject jsonGood = (JSONObject) data;
                PanelContents panelContents = new PanelContents();
                panelContents.setPanelId(2);
                panelContents.setType(2);
                panelContents.setProductId(jsonGood.getLong("item_id"));
                panelContents.setSortOrder(sortOrder ++);
                panelContents.setPicUrl(jsonGood.getString("pict_url"));
                panelContents.setSalePrice(jsonGood.getDouble("zk_final_price"));
                panelContents.setProductName(jsonGood.getString("short_title"));
                panelContents.setSubTitle(jsonGood.getString("nick"));
                panelContents.setProductImageBig(jsonGood.getString("pict_url"));
                panelContentsList.add(panelContents);
            }
            return panelContentsList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("error");
        }
    }

}
