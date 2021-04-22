package com.kayn.recommender;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RFMTest {

    @Resource
    RFM rfm;

    @Test
    void getDiffDay() {
    }

    @Test
    void getRFM() {
        System.out.println(rfm.getRFM(1619082360269L, 1, 40.0D));
    }
}
