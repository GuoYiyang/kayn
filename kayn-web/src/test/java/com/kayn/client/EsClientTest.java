package com.kayn.client;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;


class EsClientTest {

    @Test
    void test1() {
        Map<String, Object> map = EsClient.getData("prefer_tel", "liuersha");
        if (map != null) {
            List<Map<String, Object>> preferTelList = (List<Map<String, Object>>) map.get("preferTel");
            int maxCnt = 0;
            String res = "";
            for (Map<String, Object> tel : preferTelList) {
                if (Integer.parseInt(tel.get("cnt").toString()) > maxCnt) {
                    maxCnt = Integer.parseInt(tel.get("cnt").toString());
                    res = tel.get("tel").toString();
                }
            }
            System.out.println(res);
        }
    }

    @Test
    void test2() {
        Map<String, Object> map = EsClient.getData("prefer_cat", "admin");
        if (map != null) {
            List<Map<String, Object>> preferTelList = (List<Map<String, Object>>) map.get("preferCat");
            int maxCnt = 0;
            String res = "";
            for (Map<String, Object> tel : preferTelList) {
                if (Integer.parseInt(tel.get("cnt").toString()) > maxCnt) {
                    maxCnt = Integer.parseInt(tel.get("cnt").toString());
                    res = tel.get("cat").toString();
                }
            }
            System.out.println(res);
        }
    }

    @Test
    void test3() {
        Map<String, Object> map = EsClient.getData("pay_order", "admin");
        if (map != null) {
            Double totalPrice = Double.parseDouble(map.get("totalPrice").toString());
            System.out.println(totalPrice);
        }
    }

    @Test
    void test4() {
        Map<String, Object> map = EsClient.getData("pay_order", "admin");
        Double res = null;
        if (map != null) {
            Double totalPrice = Double.parseDouble(map.get("totalPrice").toString());
            Integer payCnt = Integer.parseInt(map.get("payOrderCnt").toString());
            res = totalPrice / payCnt;
            System.out.println(res);
        }
    }

    @Test
    void test5() {
        String res = null;
        Map<String, Object> map = EsClient.getData("user_query_cnt", "admin");
        if (map != null) {
            List<Map<String, Object>> preferQueryList = (List<Map<String, Object>>) map.get("query");



            int maxCnt = 0;
            for (Map<String, Object> tel : preferQueryList) {
                if (Integer.parseInt(tel.get("cnt").toString()) > maxCnt) {
                    maxCnt = Integer.parseInt(tel.get("cnt").toString());
                    res = tel.get("query").toString();
                }
            }
            System.out.println(res);
        }
    }

}
