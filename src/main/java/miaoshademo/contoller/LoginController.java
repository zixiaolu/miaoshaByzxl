package miaoshademo.contoller;

import com.mysql.cj.util.StringUtils;
import miaoshademo.dao.MiaoshaUserDao;
import miaoshademo.result.CodeMsg;
import miaoshademo.result.Result;
import miaoshademo.service.MiaoshaUserService;
import miaoshademo.util.ValidatorUtil;
import miaoshademo.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/login")
public class LoginController {

    private static Logger log= LoggerFactory.getLogger(LoginController.class);
    @Autowired
    MiaoshaUserService userService;
    @RequestMapping("/to_login")
    public String to_login(){
        return "login";
    }
    @RequestMapping("/do_login")
    @ResponseBody
    public Result<String> do_login(HttpServletResponse response, @Valid LoginVo loginVo){
        log.info(loginVo.toString());
        String token=userService.login(response,loginVo);
        return Result.success(token);
    }


}
