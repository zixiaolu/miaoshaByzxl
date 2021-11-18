package miaoshademo.service;

import com.mysql.cj.util.StringUtils;
import com.sun.tools.internal.xjc.model.CDefaultValue;
import com.sun.tools.javac.jvm.Code;
import miaoshademo.Redis.MiaoshaUserKey;
import miaoshademo.Redis.RedisService;
import miaoshademo.dao.MiaoshaUserDao;

import miaoshademo.domain.MiaoshaUser;
import miaoshademo.exception.GlobalException;
import miaoshademo.exception.GlobalExceptionHandler;
import miaoshademo.result.CodeMsg;
import miaoshademo.util.MD5Util;
import miaoshademo.util.UUIDUtil;
import miaoshademo.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class MiaoshaUserService {


    public static final String COOKIE_NAME_TOKEN = "token";


    @Autowired
    MiaoshaUserDao miaoshaUserDao;
    @Autowired
    RedisService redisService;

    public MiaoshaUser getByid(long id) {
        //热点对象缓存
        //取缓存
        MiaoshaUser user=redisService.get(MiaoshaUserKey.getById,""+id,MiaoshaUser.class);
        if(user!=null){
            return user;
        }
        //生成Redis缓存
        user=miaoshaUserDao.getByid(id);
        if(user!=null){
            redisService.set(MiaoshaUserKey.getById,""+id,user);
        }
        return user;
    }

    public boolean updataPassword(String token,long id,String passwordNew){
        MiaoshaUser user=getByid(id);
        if(user==null){
            throw new GlobalException(CodeMsg.ACCOUNT_ERROR);
        }
        //修改密码,提交数据库
        MiaoshaUser toBeUpdata =new MiaoshaUser();
        toBeUpdata.setId(id);
        toBeUpdata.setPassword(MD5Util.FormPassToDBPass(passwordNew,user.getSalt()));
        miaoshaUserDao.update(toBeUpdata);
        //修改Redis,对于热点对象数据更新时，需要更新数据库与缓存
        user.setPassword(toBeUpdata.getPassword());
        redisService.delete(MiaoshaUserKey.getById,""+id);
        redisService.set(MiaoshaUserKey.token,token,user);
        return true;
    }

    public String login(HttpServletResponse response,LoginVo loginVo){
        if(loginVo==null){
            throw new GlobalException (CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPass=loginVo.getPassword();
        MiaoshaUser user= getByid(Long.parseLong(mobile));
        //判断手机号是否存在
        if(user==null){
            throw new GlobalException (CodeMsg.ACCOUNT_ERROR);
        }
        //验证密码
        String dbPass=user.getPassword();
        String saltDB=user.getSalt();
        String finPass= MD5Util.FormPassToDBPass(formPass,saltDB);
        if (!finPass.equals(dbPass)){
            throw new GlobalException (CodeMsg.ACCOUNT_ERROR);
        }
        //生成cookie
        //将用户信息暂存在redis中
        String token = UUIDUtil.uuid();
        addCookie(response,token,user);
        return token;
    }

    public MiaoshaUser getByToken(HttpServletResponse response,String token) {
        if(StringUtils.isEmptyOrWhitespaceOnly(token)){
            return null;
        }
        MiaoshaUser user= redisService.get(MiaoshaUserKey.token,token,MiaoshaUser.class);
        //延长有效期
        if(user!=null){
            addCookie(response,token,user);
        }
        return user;

    }
    private void addCookie(HttpServletResponse response,String token,MiaoshaUser user){
        redisService.set(MiaoshaUserKey.token,token,user);
        Cookie cookie=new Cookie(COOKIE_NAME_TOKEN,token);
        cookie.setMaxAge(MiaoshaUserKey.token.TOKEN_LIFE);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
