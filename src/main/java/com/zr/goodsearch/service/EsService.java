package com.zr.goodsearch.service;

import com.zr.goodsearch.entity.GoodSearchResponseVo;
import com.zr.goodsearch.entity.GoodsInEsEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface EsService {

   GoodSearchResponseVo findGoodsAndHeightRed(String goodsType, int pageNo, int pageSize) throws IOException;
}
