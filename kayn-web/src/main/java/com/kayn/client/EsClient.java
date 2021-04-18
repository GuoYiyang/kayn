package com.kayn.client;

import org.apache.http.HttpHost;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class EsClient {

    private static final Logger logger = LoggerFactory.getLogger(EsClient.class);

    private static final RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http")));


    public static Map<String, Object> getData(String index, String id) {
        try {
            GetRequest getRequest = new GetRequest(index, id);
            GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
            if (getResponse.isExists()) {
                Map<String, Object> sourceAsMap = getResponse.getSourceAsMap();
                logger.info("sourceAsMap" + sourceAsMap);
                return sourceAsMap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
