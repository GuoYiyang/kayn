package com.kayn.client;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

@Component
public class TaobaokeApiClient {

    /**
     * 获取数据
     * @param url 请求url
     * @param map 参数集合
     * @return String
     */
    public String getData(String url, Map<String, String> map) {
        // 创建Httpclient对象,相当于打开了浏览器
        CloseableHttpClient httpclient = HttpClients.createDefault();
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
            // 关闭浏览器
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
