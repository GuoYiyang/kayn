package com.kayn.service;

import org.apache.kafka.common.protocol.types.Field;

public interface RecommenderService {

    /**
     * 最常用手机号
     * @param username 用户名
     * @return String
     */
    String getPreferTel(String username);

    /**
     * 最常浏览标签
     * @param username 用户名
     * @return String
     */
    String getPreferCat(String username);

    /**
     * 累计消费次数
     * @param username 用户名
     * @return Integer
     */
    Integer getTotalPayCnt(String username);

    /**
     * 累计消费金额
     * @param username 用户名
     * @return Double
     */
    Double getTotalPayMoney(String username);

    /**
     * 累计平均每单消费金额
     * @param username 用户名
     * @return Double
     */
    Double getTotalPayAvg(String username);

    /**
     * 累计最多查询关键词
     * @param username 用户名
     * @return String
     */
    String getTotalMostQuery(String username);
}
