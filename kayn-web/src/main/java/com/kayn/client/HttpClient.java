package com.kayn.client;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

@Component
public class HttpClient {

    CloseableHttpClient httpclient = HttpClients.createDefault();

    /**
     * 获取数据
     * @param url 请求url
     * @param map 参数集合
     * @return String
     */
    public String getData(String url, Map<String, String> map) {
        CloseableHttpResponse response = null;
        try {
            URIBuilder uriBuilder = new URIBuilder(url);
            for(String key : map.keySet()){
                uriBuilder.setParameter(key, map.get(key));
            }
            URI uri = uriBuilder.build();
            // 创建HttpGet请求，相当于在浏览器输入地址
            HttpGet httpGet = new HttpGet(uri);
            // 执行请求，相当于敲完地址后按下回车。获取响应
            response = httpclient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == 200) {
                // 解析响应，获取数据
                return EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (URISyntaxException | IOException e) {
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
        }
        return null;
    }

    public String postData(String url, String json) {
        CloseableHttpResponse response = null;
        try{
            //创建httpPost请求
            HttpPost httpPost = new HttpPost(url);
            //给httpPost设置JSON格式的参数
            StringEntity requestEntity = new StringEntity(json,"utf-8");
            requestEntity.setContentEncoding("UTF-8");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setEntity(requestEntity);
            // 执行http请求
            response = httpclient.execute(httpPost);
            // 获取响应消息
            return EntityUtils.toString(response.getEntity(), "UTF-8");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally {
            // 关闭资源
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
