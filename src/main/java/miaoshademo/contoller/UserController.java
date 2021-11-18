package miaoshademo.contoller;

import miaoshademo.domain.MiaoshaOrder;
import miaoshademo.domain.MiaoshaUser;
import miaoshademo.domain.OrderInfo;
import miaoshademo.result.CodeMsg;
import miaoshademo.result.Result;
import miaoshademo.vo.GoodsVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user")
public class UserController {

    @RequestMapping("/info")
    @ResponseBody
    public Result<MiaoshaUser> userInfo(Model model, MiaoshaUser user){
        return Result.success(user);
    }
}
