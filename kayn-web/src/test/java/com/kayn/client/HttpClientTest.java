package com.kayn.client;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class HttpClientTest {

    @Test
    void test1() {
        HashMap<String, String> analyzerMap = new HashMap<>();
        analyzerMap.put("analyzer", "ik_smart");
        analyzerMap.put("text", "我是中国人法国人");
        String s = HttpClientTest.doPostByJson("http://localhost:9200/_analyze", JSONObject.toJSONString(analyzerMap));
        JSONArray jsonArray = JSONObject.parseObject(s).getJSONArray("tokens");
        for(Object data: jsonArray) {
            JSONObject json = (JSONObject) data;
            System.out.println(json.getString("token"));
        }
    }

    public static String doPostByJson(String url, String json) {
        // 创建HttpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try{
            //创建httpPost请求
            HttpPost httpPost = new HttpPost(url);
            //给httpPost设置JSON格式的参数
            StringEntity requestEntity = new StringEntity(json,"utf-8");
            requestEntity.setContentEncoding("UTF-8");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setEntity(requestEntity);
            // 执行http请求
            response = httpClient.execute(httpPost);
            // 获取响应消息
            resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultString;
    }

}
