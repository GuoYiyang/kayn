package com.kayn.recommender;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DataTransformTest {

    @Resource
    DataTransform dataTransform;

    @Test
    void getPreferQuery() {
        System.out.println(dataTransform.getPreferQuery("zhangsan"));
    }

    @Test
    void getPreferCat() {
        System.out.println(dataTransform.getPreferCat("zhangsan"));
    }

}
