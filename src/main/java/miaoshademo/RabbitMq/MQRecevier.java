package miaoshademo.RabbitMq;

import miaoshademo.domain.MiaoshaOrder;
import miaoshademo.domain.MiaoshaUser;
import miaoshademo.service.GoodsService;
import miaoshademo.service.MiaoshaService;
import miaoshademo.service.OrderService;
import miaoshademo.util.SerialUtil;
import miaoshademo.vo.GoodsVo;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MQRecevier {

    private static Logger log = LoggerFactory.getLogger(MQRecevier.class);

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

/*    @RabbitListener(queues = MQConfig.QUEUE)
    public void recevier(String message) {
        log.info("receive message:" + message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_Q1)
    public void recevierTopic1(String message) {
        log.info("receive topic q1 message:" + message);
    }

    @RabbitListener(queues = MQConfig.TOPIC_Q2)
    public void recevierTopic2(String message) {
        log.info("receive topic q2 message:" + message);
    }*/
    @RabbitListener(queues = MQConfig.MIAOSHA_QUEUE)
    public void recevier(String message) {
        log.info("receive message:" + message);
        MiaoshaMessage mm=SerialUtil.StringToBean(message,MiaoshaMessage.class);
        MiaoshaUser user=mm.getUser();
        long goodsId=mm.getGoodsId();

        //判断库存
        GoodsVo goods=goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if(stock<=0){
            return;
        }
        //判断是否秒杀到
        MiaoshaOrder order =orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(),goodsId);
        if(order!=null){
            return ;
        }
        //生成下订单的事务
        miaoshaService.miaosha(user,goods);
    }

}
