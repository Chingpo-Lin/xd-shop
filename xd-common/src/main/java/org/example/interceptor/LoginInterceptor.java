package org.example.interceptor;

import io.jsonwebtoken.Claims;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.example.enums.BizCodeEnum;
import org.example.model.LoginUser;
import org.example.utils.CommonUtil;
import org.example.utils.JWTUtil;
import org.example.utils.JsonData;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    public static ThreadLocal<LoginUser> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String accessToken = request.getHeader("token");
        if (accessToken == null) {
            accessToken = request.getParameter("token");
        }

        if (!StringUtils.isEmpty(accessToken)) {
            Claims claims = JWTUtil.checkJWT(accessToken);
            if (claims == null) {
                // not login
                CommonUtil.sendJsonMessage(response, JsonData.buildResult(BizCodeEnum.ACCOUNT_NOT_LOGIN));
                return false;
            }
            long userId = Long.valueOf(claims.get("id").toString());
            String headImg = (String) claims.get("head_img");
            String name = (String) claims.get("name");
            String mail = (String) claims.get("mail");

            LoginUser loginUser = LoginUser
                    .builder()
                    .headImg(headImg)
                    .name(name)
                    .id(userId)
                    .mail(mail)
                    .build();

//            loginUser.setName(name);
//            loginUser.setId(userId);
//            loginUser.setHeadImg(headImg);
//            loginUser.setMail(mail);

            // use attribute to get user info
            // request.setAttribute("loginUser", loginUser);

            // use ThreadLocal to pass user info TODO
            threadLocal.set(loginUser);
            return true;
        }
        CommonUtil.sendJsonMessage(response, JsonData.buildResult(BizCodeEnum.ACCOUNT_NOT_LOGIN));
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        threadLocal.remove();
    }
}
