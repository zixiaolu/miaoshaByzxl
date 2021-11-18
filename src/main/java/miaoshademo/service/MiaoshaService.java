package miaoshademo.service;

import miaoshademo.Redis.MiaoshaKey;
import miaoshademo.Redis.RedisService;
import miaoshademo.domain.Goods;
import miaoshademo.domain.MiaoshaOrder;
import miaoshademo.domain.MiaoshaUser;
import miaoshademo.domain.OrderInfo;
import miaoshademo.util.MD5Util;
import miaoshademo.util.UUIDUtil;
import miaoshademo.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;

@Service
public class MiaoshaService {

    @Autowired
    GoodsService goodsService;
    @Autowired
    OrderService orderService;
    @Autowired
    RedisService redisService;

    /*
    * QPS:444
    *
    *5000*10
    * */
    @Transactional
    public OrderInfo miaosha(MiaoshaUser user, GoodsVo goods) {

        //减库存
        boolean success=goodsService.reduceStock(goods);
        //下订单
        if(success) {
            return orderService.createOrder(user, goods);
        }
        else{
            setGoodsOver(goods.getId());
            return null;
        }
    }
    public long getMiaoshaResult(Long userId, long goodsId) {

       MiaoshaOrder order=orderService.getMiaoshaOrderByUserIdGoodsId(userId,goodsId);
       if(order!=null){
           return order.getOrderId();
       }
       else{
           boolean isOver=getGoodsOver(goodsId);
           if(isOver){
               return -1;
           }
           else{
               return 0;
           }
       }
    }

    private void setGoodsOver(Long id) {
        redisService.set(MiaoshaKey.IS_GOOD_OVER,""+id,true);
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(MiaoshaKey.IS_GOOD_OVER,""+goodsId);
    }

    public boolean check(MiaoshaUser user, long goodsId, String path) {
        if(user==null||path==null){
            return false;
        }
        String pathInRedis=redisService.get(MiaoshaKey.GET_MIAOSHA_PATH,""+user.getId()+"_"+goodsId,String.class);
        return pathInRedis.equals(path);
    }


    public String createMiaoshaPath(MiaoshaUser user, long goodsId) {

        String str= MD5Util.md5(UUIDUtil.uuid()+"1a2b3c4d");
        redisService.set(MiaoshaKey.GET_MIAOSHA_PATH,""+user.getId()+"_"+goodsId,str);
        return str;
    }
}
