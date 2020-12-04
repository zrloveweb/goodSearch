package com.zr.goodsearch.controller;

import com.zr.goodsearch.entity.GoodSearchResponseVo;
import com.zr.goodsearch.service.impl.EsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class EsController {


    @Autowired
    private EsServiceImpl esService;

    @CrossOrigin("*")
    @GetMapping("/searchGoods/{name}/{pageNo}/{pageSize}")
    public GoodSearchResponseVo searchGoods(@PathVariable("name") String name,
                            @PathVariable("pageNo") int pageNo,
                            @PathVariable("pageSize") int pageSize) {
        GoodSearchResponseVo searchData = null;
        try {
            searchData = esService.findGoodsAndHeightRed(name, pageNo, pageSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return searchData;
    }
}
