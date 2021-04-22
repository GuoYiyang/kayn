package com.kayn.util;

import com.kayn.recommender.TFIDF;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class IDFUtilTest {

    @Resource
    TFIDF idfUtil;


    @Test
    void getTaobaoGoodCnt() {
        System.out.println(idfUtil.getTaobaoGoodCnt("java"));
    }

    @Test
    void getIDF() {
        System.out.println(idfUtil.getTFIDF("java", 12));
    }
}
