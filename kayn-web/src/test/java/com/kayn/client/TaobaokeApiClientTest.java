package com.kayn.client;

import com.kayn.pojo.good.PanelResult;
import com.kayn.result.Result;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.HashMap;

@SpringBootTest
class TaobaokeApiClientTest {

    @Autowired
    private HttpClient client;


    @Test
    void test1() {

        // 创建Httpclient对象,相当于打开了浏览器
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创建HttpGet请求，相当于在浏览器输入地址
        HttpGet httpGet = new HttpGet("https://api.taobaokeapi.com/?usertoken=0c0d1cc55a4226364c0a914a5a04223d&method=taobao.tbk.sc.material.optional&site_id=2255900096&adzone_id=111236250012&q=男士");

        CloseableHttpResponse response = null;
        try {
            // 执行请求，相当于敲完地址后按下回车。获取响应
            response = httpclient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                // 解析响应，获取数据
                String content = EntityUtils.toString(response.getEntity(), "UTF-8");
//                return content;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // 关闭浏览器
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    void test2() {
        HashMap<String, String> map = new HashMap<>();
        map.put("usertoken", "0c0d1cc55a4226364c0a914a5a04223d");
        map.put("method", "taobao.tbk.sc.material.optional");
        map.put("site_id", "2255900096");
        map.put("adzone_id", "111236250012");
        map.put("q", "男士");
        String url = "https://api.taobaokeapi.com";
        String data = client.getData(url, map);
    }

    @Test
    void test3() {
        HashMap<String, String> map = new HashMap<>();
        map.put("usertoken", "0c0d1cc55a4226364c0a914a5a04223d");
        map.put("method", "taobao.tbk.sc.material.optional");
        map.put("site_id", "2255900096");
        map.put("adzone_id", "111236250012");
        map.put("q", "男士");
    }

    @Test
    void test4() {
        Result<PanelResult> result = new Result<>();
        PanelResult panelResult = new PanelResult();

        HashMap<String, String> map = new HashMap<>();
        map.put("method", "taobao.tbk.item.recommend.get");
        map.put("usertoken", "0c0d1cc55a4226364c0a914a5a04223d");
        map.put("num_iids", "598161855552");
        String url = "https://api.taobaokeapi.com";

        String res = client.getData(url, map);
        System.out.println(res);
    }

    @Test
    void test5() {
        HashMap<String, String> map = new HashMap<>();
        map.put("usertoken", "0c0d1cc55a4226364c0a914a5a04223d");
        map.put("method", "taobao.tbk.content.get");
        map.put("site_id", "2255900096");
        map.put("adzone_id", "111236250012");
        map.put("type", "1");
        String url = "https://api.taobaokeapi.com";
        String data = client.getData(url, map);
        System.out.println(data);
    }


}
