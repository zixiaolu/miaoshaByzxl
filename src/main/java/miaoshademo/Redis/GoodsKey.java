package miaoshademo.Redis;

import miaoshademo.domain.Goods;

public class GoodsKey extends BasePrefix{

    public static GoodsKey getGoodsList=new GoodsKey(30,"gl");
    public static GoodsKey getGoodsDetail=new GoodsKey(30,"gd");
    public static GoodsKey getMiaoshaGoodsStock=new GoodsKey(0,"gs");

    public GoodsKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

}
