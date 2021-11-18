package miaoshademo.contoller;

import miaoshademo.RabbitMq.MQRecevier;
import miaoshademo.RabbitMq.MQSender;
import miaoshademo.Redis.RedisService;
import miaoshademo.Redis.UserKey;
import miaoshademo.domain.User;
import miaoshademo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import miaoshademo.result.CodeMsg;
import miaoshademo.result.Result;

@Controller
@RequestMapping("/demo")
public class DemoController {
    @Autowired
    UserService userService;
    @Autowired
    RedisService redisService;
    @Autowired
    MQSender mqSender;
    @Autowired
    MQRecevier mqRecevier;


    @RequestMapping("/helloworld")
    @ResponseBody
    String home(){
        return "Hello World!";
    }
    @RequestMapping("/hello")
    @ResponseBody
    public Result<String> hello(){
        return Result.success("hello myfriend");
    }
    @RequestMapping("/helloError")
    @ResponseBody
    public Result<String> helloerror(){
        return Result.error(CodeMsg.SERVER_ERROR);
    }
    @RequestMapping("/thy")
    public String thymeleaf(Model model){
        model.addAttribute("name","zxl");
        return "hello";
    }
    @RequestMapping("/db/get")
    @ResponseBody
    public Result<User> dbGet(){
        User user=userService.getById(1);
        return Result.success(user);
    }

    @RequestMapping("/db/tx")
    @ResponseBody
    public Result<Boolean> dbtx(){
        userService.tx();
        return Result.success(true);
    }

    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<User> redisGet(){
        User v1=redisService.get(UserKey.getById,""+1,User.class);
        return Result.success(v1);
    }

    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> redisSet(){
        User user=new User();
        user.setName("zxl");
        user.setId(1);
        boolean v1=redisService.set(UserKey.getById,""+1,user);
        return Result.success(true);
    }

}
