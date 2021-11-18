package miaoshademo.vo;

import miaoshademo.domain.Goods;
import miaoshademo.domain.OrderInfo;

public class OrderDetailVo {
    private Goods goods;
    private OrderInfo orderInfo;

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public OrderInfo getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(OrderInfo orderInfo) {
        this.orderInfo = orderInfo;
    }
}
