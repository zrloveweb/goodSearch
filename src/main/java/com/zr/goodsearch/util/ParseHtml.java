package com.zr.goodsearch.util;

import com.zr.goodsearch.entity.GoodsInEsEntity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParseHtml {

    public static List<GoodsInEsEntity> getListGoods() {
        //从URL加载HTML
        Document document = null;
        List<GoodsInEsEntity> listGoodsEs = new ArrayList<GoodsInEsEntity>();

        try {
            document = Jsoup.connect("https://search.jd.com/Search?keyword=mysql&enc=utf-8&wq=mysql").get();
            Element goodsList = document.getElementById("J_goodsList");
            Elements ulBook = goodsList.getElementsByClass("gl-warp clearfix");
            Element firstUlBook = ulBook.get(0);
            Elements li = firstUlBook.getElementsByTag("li");
            GoodsInEsEntity goodEs = null;
            for (Element el : li) {
                goodEs = new GoodsInEsEntity();
                goodEs.setImg(el.getElementsByTag("img").eq(0).attr("data-lazy-img"));
                goodEs.setName(el.getElementsByClass("p-name").eq(0).text());
                goodEs.setPrice(el.getElementsByClass("p-price").eq(0).text());
                listGoodsEs.add(goodEs);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listGoodsEs;
    }
}
