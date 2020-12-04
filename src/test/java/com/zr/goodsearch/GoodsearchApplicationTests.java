package com.zr.goodsearch;

import com.alibaba.fastjson.JSON;
import com.zr.goodsearch.entity.GoodsInEsEntity;
import com.zr.goodsearch.util.ParseHtml;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class GoodsearchApplicationTests {

    @Autowired
    private RestHighLevelClient client;

    @Test
    void batchSave()  {
        BulkRequest request = new BulkRequest();
        request.timeout("10s");

        List<GoodsInEsEntity> listGoods = ParseHtml.getListGoods();

        // 批处理请求， 修改，删除，只要在这里修改相应的请求就可以
        for (int i = 0; i < listGoods.size(); i++) {
            request.add(new IndexRequest("goods")
                    .source(JSON.toJSONString(listGoods.get(i)), XContentType.JSON));
        }

        BulkResponse bulkResponse = null;
        try {
            bulkResponse = client.bulk(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //是否失败，返回false表示成功
        System.out.println(bulkResponse.hasFailures());
        System.out.println(JSON.toJSONString(listGoods));
    }

    @Test
    void contextLoads() throws IOException {
        GoodsInEsEntity user = new GoodsInEsEntity("1","aa","xx","223");
        IndexRequest request = new IndexRequest("goods");
        // 规则 PUT /index/_doc/1
        request.id("1");
        request.timeout(TimeValue.timeValueSeconds(1));
        // 将数据放入请求 json
        request.source(JSON.toJSONString(user), XContentType.JSON);
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        System.out.println(response.toString());
        System.out.println(response.status());
    }

    @Test
    void findTotal() throws IOException {
        // 条件搜索
        SearchRequest searchRequest = new SearchRequest("goods");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 分页
        sourceBuilder.from(30);
        sourceBuilder.size(10);
        // 精准匹配
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "java");
        sourceBuilder.query(termQueryBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        // 高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("name");
        highlightBuilder.requireFieldMatch(false);   // 多个高亮显示！
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        sourceBuilder.highlighter(highlightBuilder);

        // 执行搜索
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(searchResponse.getHits().getTotalHits());
        for (SearchHit documentField : searchResponse.getHits().getHits()) {

            System.out.println(documentField.getSourceAsMap());
        }
    }


    @Test
    void find() throws IOException {
        GetRequest request = new GetRequest("goods", "1");
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        System.out.println(response.getSourceAsString());
        System.out.println(response);
    }

    @Test
    void delete() throws IOException {
        DeleteRequest request = new DeleteRequest("goods", "1");
        request.timeout("1s");

        DeleteResponse deleteResponse = client.delete(request, RequestOptions.DEFAULT);
        System.out.println(deleteResponse);
    }

}
