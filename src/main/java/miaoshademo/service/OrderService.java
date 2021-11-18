package miaoshademo.service;


import miaoshademo.Redis.OrderKey;
import miaoshademo.Redis.RedisService;
import miaoshademo.dao.GoodsDao;
import miaoshademo.dao.OrderDao;
import miaoshademo.domain.MiaoshaOrder;
import miaoshademo.domain.MiaoshaUser;
import miaoshademo.domain.OrderInfo;
import miaoshademo.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Date;

@Service
public class OrderService {

    @Autowired
    OrderDao orderDao;
    @Autowired
    RedisService redisService;


    public OrderInfo getOrderById(long orderId) {
        return orderDao.getOrderById(orderId);
    }

    @Transactional
    public OrderInfo createOrder(MiaoshaUser user, GoodsVo goods) {

        OrderInfo orderInfo =new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goods.getId());
        orderInfo.setGoodsName(goods.getGoodsName());
        orderInfo.setGoodsPrice(goods.getMiaoshaPrice());
        orderInfo.setOrderChannel(1);
        orderInfo.setStatus(0);
        orderInfo.setUserId(user.getId());
        //
        try{
        orderDao.insert(orderInfo);
        MiaoshaOrder miaoshaOrder=new MiaoshaOrder();
        miaoshaOrder.setGoodsId(goods.getId());
        miaoshaOrder.setOrderId(orderInfo.getId());
        miaoshaOrder.setUserId(user.getId());
        orderDao.insertMiaoshaOrder(miaoshaOrder);
        redisService.set(OrderKey.getMiaoshaOrderByUidGid,""+user.getId()+"_"+goods.getId(),miaoshaOrder);
        }
        catch (Exception e){
            throw e;
        }
        finally  {
            return orderInfo;
        }
        //将秒杀订单写入redis中
    }

    public MiaoshaOrder getMiaoshaOrderByUserIdGoodsId(long userid, long goodsId) {
        /*return orderDao.getMiaoshaOrderByUserIdGoodsId(userid,goodsId);*/
        return redisService.get(OrderKey.getMiaoshaOrderByUidGid,""+userid+"_"+goodsId,MiaoshaOrder.class);
    }
}
