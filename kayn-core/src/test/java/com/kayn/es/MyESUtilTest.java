package com.kayn.es;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.MultiSearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.Test;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;

import java.io.IOException;
import java.util.*;

@Slf4j
class MyESUtilTest {
    RestHighLevelClient client = MyESUtil.getClient();

    @Test
    public void createData() {
        try {
//            Map<String, Object> jsonMap = new HashMap<>();
//            jsonMap.put("user", "kimchy");
//            jsonMap.put("postDate", new Date());
//            jsonMap.put("message", "trying out Elasticsearch");
//            IndexRequest indexRequest = new IndexRequest("posts")
//                    .id("1").source(jsonMap);
            IndexRequest request = new IndexRequest("test1")
                    .id("1")
                    .source("user", "slimshady",
                            "postDate", System.currentTimeMillis(),
                            "message", "hello elastic");

            IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
            log.info("indexResponse:" + indexResponse);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void getData() {
        try {
            GetRequest getRequest = new GetRequest("test1", "1");
            MultiSearchRequest multiSearchRequest = new MultiSearchRequest();
            GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
            if (getResponse.isExists()) {
                Map<String, Object> sourceAsMap = getResponse.getSourceAsMap();
                log.info("sourceAsMap" + sourceAsMap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void updateData() {
        try {
            UpdateRequest updateRequest = new UpdateRequest("test1", "1");
            updateRequest.doc("message", "hello");
            UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);
            log.info("updateResponse" + updateResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void addData() {
        try {
            IndexRequest request = new IndexRequest("test1");
            request.source("user", "kayn",
                            "postData", System.currentTimeMillis(),
                            "message", "i am kayn");
            IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
            log.info("indexResponse" + indexResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void deleteData() {
        try {
            DeleteRequest request = new DeleteRequest("test1");
            DeleteResponse deleteResponse = client.delete(request, RequestOptions.DEFAULT);
            log.info("deleteResponse" + deleteResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
