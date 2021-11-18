package miaoshademo.contoller;


import miaoshademo.domain.Goods;
import miaoshademo.domain.MiaoshaUser;
import miaoshademo.domain.OrderInfo;
import miaoshademo.result.CodeMsg;
import miaoshademo.result.Result;
import miaoshademo.service.GoodsService;
import miaoshademo.service.OrderService;
import miaoshademo.vo.GoodsDetailVo;
import miaoshademo.vo.GoodsVo;
import miaoshademo.vo.OrderDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;

    @RequestMapping("/detail")
    @ResponseBody
    public Result<OrderDetailVo> orderDetail(Model model, MiaoshaUser user, @RequestParam("orderId") long orderId){

        if(user==null){
            return Result.error(CodeMsg.LOGIN_ERROR);
        }
        OrderInfo orderInfo=orderService.getOrderById(orderId);
        if(orderInfo==null){
            return Result.error(CodeMsg.OREDER_NOT_EXIST);
        }
        long goodsId= orderInfo.getGoodsId();
        GoodsVo goods=goodsService.getGoodsVoByGoodsId(goodsId);
        OrderDetailVo orderDetailVo=new OrderDetailVo();
        orderDetailVo.setGoods(goods);
        orderDetailVo.setOrderInfo(orderInfo);
        return Result.success(orderDetailVo);
    }
}
