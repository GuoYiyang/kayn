package com.kayn.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RecommenderServiceTest {

    @Resource
    RecommenderService recommenderService;

    String username = "admin";

    @Test
    void getPreferTel() {
        System.out.println(recommenderService.getPreferTel(username));
    }

    @Test
    void getPreferCat() {
        System.out.println(recommenderService.getPreferCat(username));
    }

    @Test
    void getTotalPayCnt() {
        System.out.println(recommenderService.getTotalPayCnt(username));
    }

    @Test
    void getTotalPayMoney() {
        System.out.println(recommenderService.getTotalPayMoney(username));
    }

    @Test
    void getTotalPayAvg() {
        System.out.println(recommenderService.getTotalPayAvg(username));
    }

    @Test
    void getTotalMostQuery() {
        System.out.println(recommenderService.getTotalMostQuery(username));
    }
}
