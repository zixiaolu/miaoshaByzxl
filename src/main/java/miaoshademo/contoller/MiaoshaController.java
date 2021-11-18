package miaoshademo.contoller;

import miaoshademo.RabbitMq.MQSender;
import miaoshademo.RabbitMq.MiaoshaMessage;
import miaoshademo.Redis.GoodsKey;
import miaoshademo.Redis.MiaoshaKey;
import miaoshademo.Redis.OrderKey;
import miaoshademo.Redis.RedisService;
import miaoshademo.domain.MiaoshaOrder;
import miaoshademo.domain.MiaoshaUser;
import miaoshademo.domain.OrderInfo;
import miaoshademo.result.CodeMsg;
import miaoshademo.result.Result;
import miaoshademo.service.GoodsService;
import miaoshademo.service.MiaoshaService;
import miaoshademo.service.MiaoshaUserService;
import miaoshademo.service.OrderService;
import miaoshademo.util.MD5Util;
import miaoshademo.util.UUIDUtil;
import miaoshademo.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender mqSender;

    //进行本地标记减少访问库存时需要跟redis的交互
    private Map<Long,Boolean> localOverMap= new HashMap<Long, Boolean>();

    /**
     * 该方法继承于Springboot
     * 在初始化Springboot时执行一些操作
     *
     *
     *
     * Redis预减库存，将商品数量初始化加入数据库中
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {

        List<GoodsVo> goodsList=goodsService.listGoodsVo();
        if(goodsList==null){
            return;
        }

        for(GoodsVo goods:goodsList){
            redisService.set(GoodsKey.getMiaoshaGoodsStock,""+goods.getId(),goods.getStockCount());
            localOverMap.put(goods.getId(), false);
        }

    }

    /*
    * GET与POST区别
    * GET是幂等，从服务端获取静态数据，不改变服务端数据
    * POST会改变服务端的数据

    * 优化后
    * 1000
    * */
    @PostMapping("/{path}/do_miaosha")
    @ResponseBody
    public Result<Integer> domiaosha(Model model, MiaoshaUser user, @RequestParam("goodsId") long goodsId,@PathVariable("path") String path ) {

        model.addAttribute("user", user);
        if (user == null) {
            return Result.error(CodeMsg.LOGIN_ERROR);
        }
        //验证Path是否正确
        boolean RightPath=miaoshaService.check(user,goodsId,path);
        if(!RightPath){
            return Result.error(CodeMsg.ILLEGAL_REQUST);
        }
        //内存标记减少访问redis数据库的消耗
        boolean over=localOverMap.get(goodsId);
        if(over){
            return Result.error(CodeMsg.MIAOSHA_END);
        }
        //1.判断是否秒杀到
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            return Result.error(CodeMsg.MIAOSHA_ONCE);
        }
        //2.预减库存
        long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, "" + goodsId);
        if (stock < 0) {
            localOverMap.put(goodsId,true);
            return Result.error(CodeMsg.MIAOSHA_END);
        }
        //3.入队
        MiaoshaMessage mm = new MiaoshaMessage();
        mm.setUser(user);
        mm.setGoodsId(goodsId);
        mqSender.sendMiaoshaMessage(mm);
        return Result.success(0);//返回0代表秒杀排队中

    }
        /**
         *orderId: 成功
         * -1：库存不足，秒杀失败
         * 0：排队中
         */
        @RequestMapping(value = "/result",method = RequestMethod.GET)
        @ResponseBody
        public Result<Long> miaoshaResult(Model model, MiaoshaUser user, @RequestParam("goodsId") long goodsId){

            model.addAttribute("user",user);
            if(user==null){
                return Result.error(CodeMsg.LOGIN_ERROR);
            }
            long result =miaoshaService.getMiaoshaResult(user.getId(),goodsId);
            return Result.success(result);
        }

        @RequestMapping(value = "/path",method = RequestMethod.GET)
        @ResponseBody
        public Result<String> GetMiaoshaPath(Model model, MiaoshaUser user, @RequestParam("goodsId") long goodsId){
            model.addAttribute(user);
            if(user==null){
                return Result.error(CodeMsg.LOGIN_ERROR);
            }
            String path=miaoshaService.createMiaoshaPath(user,goodsId);
            return Result.success(path);
        }

}
