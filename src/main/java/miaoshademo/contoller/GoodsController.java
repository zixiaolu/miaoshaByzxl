package miaoshademo.contoller;

import com.mysql.cj.util.StringUtils;
import miaoshademo.Redis.GoodsKey;
import miaoshademo.Redis.RedisService;
import miaoshademo.domain.Goods;
import miaoshademo.domain.MiaoshaUser;
import miaoshademo.domain.User;
import miaoshademo.result.Result;
import miaoshademo.service.GoodsService;
import miaoshademo.service.MiaoshaUserService;
import miaoshademo.service.UserService;
import miaoshademo.vo.GoodsDetailVo;
import miaoshademo.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.context.webflux.SpringWebFluxContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    MiaoshaUserService userService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    RedisService redisService;

    //使用Thymeleaf做前端时，Springboot可以使用该解析器进行前端渲染
    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;
    /*
    * before:
    * QPS:1541
    * 5000*10
    *
    * */
    @RequestMapping(value = "/to_list",produces = "text/html")
    @ResponseBody
    public String tolist(HttpServletRequest request, HttpServletResponse response, Model model, MiaoshaUser user){
        model.addAttribute("user",user);
        //在redis中取出页面缓存
        String html= redisService.get(GoodsKey.getGoodsList,"",String.class);
        if(!StringUtils.isEmptyOrWhitespaceOnly(html)){
            return html;
        }
        List<GoodsVo> goodsList=goodsService.listGoodsVo();
        model.addAttribute("goodsList",goodsList);
        //在redis中取出页面缓存

       //如果redis中没有页面缓存，手动渲染
        //使用IWebContext 实现 生成Context
        IWebContext ctx=new WebContext(request,response, request.getServletContext(),request.getLocale(),model.asMap());
        html =thymeleafViewResolver.getTemplateEngine().process("goods_list",ctx);
        if(!StringUtils.isNullOrEmpty(html)){
            redisService.set(GoodsKey.getGoodsList,"",html);
        }
        return html;
    }
    //秒杀商品详情
    @RequestMapping(value = "/to_detail2/{goodsId}",produces = "text/html")
    @ResponseBody
    public String todetail2(HttpServletRequest request, HttpServletResponse response,Model model, MiaoshaUser user,
                           @PathVariable("goodsId")long goodsId){

        model.addAttribute("user",user);
        //取缓存
        String html=redisService.get(GoodsKey.getGoodsDetail,""+goodsId,String.class);
        if(!StringUtils.isNullOrEmpty(html)){
            return html;
        }
        //手动渲染
        GoodsVo goods= goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods",goods);
        long startAt=goods.getStartDate().getTime();
        long endAt=goods.getEndDate().getTime();
        long now=System.currentTimeMillis();
        int miaoshaStatus=0;
        int remainSeconds=0;
        int stockCount=goods.getStockCount();
        if(now<startAt){
            miaoshaStatus=0;
            remainSeconds=(int)(startAt-now)/1000;
        }
        else if(now>endAt){
            miaoshaStatus=2;
            remainSeconds=-1;
        }
        else{
            miaoshaStatus=1;
            remainSeconds=0;

        }
        model.addAttribute("miaoshaStatus",miaoshaStatus);
        model.addAttribute("remainSeconds",remainSeconds);
        model.addAttribute("stockCount",stockCount);
        IWebContext ctx=new WebContext(request,response, request.getServletContext(),request.getLocale(),model.asMap());
        html =thymeleafViewResolver.getTemplateEngine().process("goods_detail",ctx);
        if(!StringUtils.isNullOrEmpty(html)){
            redisService.set(GoodsKey.getGoodsDetail,""+goodsId,html);
        }
        return html;
    }

    @RequestMapping(value = "/detail/{goodsId}")
    @ResponseBody
    public Result<GoodsDetailVo> todetail(HttpServletRequest request, HttpServletResponse response, Model model, MiaoshaUser user,
                                          @PathVariable("goodsId")long goodsId){
        //手动渲染
        GoodsVo goods= goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods",goods);
        long startAt=goods.getStartDate().getTime();
        long endAt=goods.getEndDate().getTime();
        long now=System.currentTimeMillis();
        int miaoshaStatus=0;
        int remainSeconds=0;
        if(now<startAt){
            miaoshaStatus=0;
            remainSeconds=(int)(startAt-now)/1000;
        }
        else if(now>endAt){
            miaoshaStatus=2;
            remainSeconds=-1;
        }
        else{
            miaoshaStatus=1;
            remainSeconds=0;

        }
        model.addAttribute("miaoshaStatus",miaoshaStatus);
        model.addAttribute("remainSeconds",remainSeconds);
        GoodsDetailVo vo=new GoodsDetailVo();
        vo.setGoods(goods);
        vo.setMiaoshaStatus(miaoshaStatus);
        vo.setRemainSeconds(remainSeconds);
        vo.setUser(user);
        return Result.success(vo);
    }


}