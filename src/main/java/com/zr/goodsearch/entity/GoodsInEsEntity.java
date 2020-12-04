package com.zr.goodsearch.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsInEsEntity {
    private String id;
    private String img;
    private String name;
    private String price;
}
