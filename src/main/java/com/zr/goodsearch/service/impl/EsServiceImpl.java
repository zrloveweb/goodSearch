package com.zr.goodsearch.service.impl;

import com.alibaba.fastjson.JSON;
import com.zr.goodsearch.entity.GoodSearchResponseVo;
import com.zr.goodsearch.entity.GoodsInEsEntity;
import com.zr.goodsearch.service.EsService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class EsServiceImpl implements EsService {

    @Autowired
    private RestHighLevelClient client;

    @Override
    public GoodSearchResponseVo findGoodsAndHeightRed(String name, int pageNo, int pageSize) throws IOException {
        if (pageNo <= 1) {
            pageNo = 1;
        }
        //如果小于1从1开始，大于1 减去1 乘以10
        pageNo = pageNo <= 1 ? pageNo = 1 : (pageNo - 1) * pageSize;
        // 条件搜索
        SearchRequest searchRequest = new SearchRequest("goods");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 分页
        sourceBuilder.from(pageNo);
        sourceBuilder.size(pageSize);
        // 精准匹配
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", name);
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

        // 解析结果
        List<GoodsInEsEntity> list = new ArrayList<>();
        for (SearchHit documentField : searchResponse.getHits().getHits()) {
            Map<String, HighlightField> highlightFields = documentField.getHighlightFields();
            HighlightField title = highlightFields.get("name");
            Map<String, Object> sourceAsMap = documentField.getSourceAsMap();   // 原来的结果
            // 解析高亮的字段， 将原来的字段换为我们高亮的字段即可！
            if (title != null) {
                Text[] fragments = title.fragments();
                String n_title = "";
                for (Text text : fragments) {
                    n_title += text;
                }
                sourceAsMap.put("name", n_title);
            }
            GoodsInEsEntity goodsInEsEntity = JSON.parseObject(JSON.toJSONString(sourceAsMap), GoodsInEsEntity.class);
            list.add(goodsInEsEntity);
        }
        GoodSearchResponseVo goodSearchResponseVo = new GoodSearchResponseVo();
        goodSearchResponseVo.setData(list);
        goodSearchResponseVo.setTotal(searchResponse.getHits().getTotalHits().value);
        return goodSearchResponseVo;
    }
}
