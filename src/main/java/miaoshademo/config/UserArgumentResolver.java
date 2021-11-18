package miaoshademo.config;

import com.mysql.cj.util.StringUtils;
import miaoshademo.domain.MiaoshaUser;
import miaoshademo.service.MiaoshaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Service
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    MiaoshaUserService userService;

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        Class<?> clazz=methodParameter.getParameterType();
        return clazz == MiaoshaUser.class;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        HttpServletRequest request=nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response=nativeWebRequest.getNativeResponse(HttpServletResponse.class);

        String paramToken=request.getParameter(MiaoshaUserService.COOKIE_NAME_TOKEN);
        String cookieToken=getCookieValue(request,MiaoshaUserService.COOKIE_NAME_TOKEN);

        if(StringUtils.isEmptyOrWhitespaceOnly(cookieToken)&&StringUtils.isEmptyOrWhitespaceOnly(paramToken)){
            return null;
        }

        String token=StringUtils.isEmptyOrWhitespaceOnly(paramToken)?cookieToken:paramToken;//paramToken与cookieToken之间谁存在取谁
        MiaoshaUser user=userService.getByToken(response,token);
        return user;
    }

    private String getCookieValue(HttpServletRequest request, String cookieNameToken) {
        Cookie[] cookies=request.getCookies();
        if(cookies==null||cookies.length<=0){
            return null;
        }
        for(Cookie c:cookies){
            if(c.getName().equals(cookieNameToken)){
                return c.getValue();
            }
        }
        return null;
    }
}
