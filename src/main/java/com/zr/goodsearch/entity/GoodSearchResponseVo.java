package com.zr.goodsearch.entity;

import lombok.Data;

import java.util.List;

@Data
public class GoodSearchResponseVo {

    private List<GoodsInEsEntity> data;
    private long total;

}
