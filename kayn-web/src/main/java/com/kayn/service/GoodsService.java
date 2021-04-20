package com.kayn.service;

import com.kayn.pojo.good.GoodDetail;
import com.kayn.pojo.good.GoodPage;
import com.kayn.pojo.good.PanelResult;
import com.kayn.result.Result;

import java.util.List;

public interface GoodsService {

    /**
     * 查询淘宝所有商品信息
     * @param q 关键词
     * @param pageSize 页大小
     * @param pageNo 页码
     * @param sort 排序
     * @param priceGt 价格最大值
     * @param priceLte 价格最小值
     * @return Result<GoodPage>
     */
    Result<GoodPage> getGoodPage(String q, Integer pageSize, Integer pageNo, Integer sort, Double priceGt, Double priceLte);

    /**
     * 查询淘宝商品详情
     * @param productId 商品ID
     * @return Result<GoodDetail>
     */
    Result<GoodDetail> getGoodDetail(Long productId);

    /**
     * 获取首页推荐
     * @param username 用户名
     * @return Result<PanelResult>
     */
    Result<List<PanelResult>> getGoodHome(String username);


    Result<PanelResult> getRecommend(Long productId);
}
