package com.kayn.controller;

import com.alibaba.fastjson.JSONObject;
import com.kayn.pojo.good.GoodDetail;
import com.kayn.pojo.good.GoodPage;
import com.kayn.pojo.good.PanelResult;
import com.kayn.recommender.DataTransform;
import com.kayn.result.Result;
import com.kayn.service.GoodsService;
import com.kayn.service.RecommenderService;
import com.kayn.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/goods")
public class GoodsController {

    private static final Logger logger = LoggerFactory.getLogger(GoodsController.class);

    @Resource
    private GoodsService goodsService;

    @Resource
    private DataTransform dataTransform;

    /**
     * 检索商品页接口
     * @param q 关键词
     * @param pageSize 页大小
     * @param pageNo 页码
     * @param sort 排序
     * @param priceGt 价格最大值
     * @param priceLte 价格最小值
     * @return Result<GoodPage>
     */
    @GetMapping("/allGoods")
    public Result<GoodPage> getGoodPage(@RequestParam(value = "username", required = false) String username,
                                        @RequestParam(value = "q") String q,
                                        @RequestParam("size") Integer pageSize,
                                        @RequestParam("page") Integer pageNo,
                                        @RequestParam(value = "sort", required = false) Integer sort,
                                        @RequestParam(value = "priceGt", required = false) Double priceGt,
                                        @RequestParam(value = "priceLte", required = false) Double priceLte) {
        if (username != null) {
            String preferQuery = dataTransform.getPreferQuery(username);
            if (q.equals("")) {
                if (preferQuery != null) {
                    q = preferQuery;
                } else {
                    q = "默认";
                }
            }
            HashMap<String, String> info = new HashMap<>();
            info.put("username", username);
            info.put("query", q);
            logger.info(JSONObject.toJSONString(info));
        } else {
            q = "默认";
        }
        return goodsService.getGoodPage(q, pageSize, pageNo, sort, priceGt, priceLte);
    }

    /**
     * 首页数据接口
     * @return String
     */
    @GetMapping("/home")
    public Result<List<PanelResult>> getGoodHome(@RequestParam(value = "username", required = false) String username) {
        if (username != null) {
            return goodsService.getGoodHome(username);
        } else {
            return goodsService.getGoodHome("测试用户");
        }
    }

    /**
     * 获取商品详情
     * @param jsonObject {productId: xxx, username: xxx}
     * @return Result<GoodDetail>
     */
    @PostMapping("/productDet")
    public Result<GoodDetail> getGoodDetail(@RequestBody JSONObject jsonObject) {
        Long productId = jsonObject.getLong("productId");
        String username = jsonObject.getString("username");
        Result<GoodDetail> result = goodsService.getGoodDetail(productId);
        HashMap<String, String> info = new HashMap<>();
        info.put("username", username);
        info.put("catName", result.getResult().getCatName());
        info.put("productName", result.getResult().getProductName());
        logger.info(JSONObject.toJSONString(info));
        return result;
    }

    /**
     * 获取商品推荐
     * @param username 用户名
     * @return Result<PanelResult>
     */
    @GetMapping("/recommend")
    public Result<List<PanelResult>> getRecommend(@RequestParam(value = "username", required = false) String username) {
        if (username != null) {
            return goodsService.getRecommend(username);
        } else {
            return goodsService.getRecommend("测试用户");
        }
    }
}
