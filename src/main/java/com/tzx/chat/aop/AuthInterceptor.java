package com.tzx.chat.aop;

import com.tzx.chat.annotation.AuthCheck;
import com.tzx.chat.component.RedisComponent;
import com.tzx.chat.entiy.domain.UserInfo;
import com.tzx.chat.entiy.enums.ErrorCode;
import com.tzx.chat.entiy.vo.user.LoginUserVO;
import com.tzx.chat.exception.BusinessException;
import com.tzx.chat.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Slf4j
@Aspect //表示这是一个切面
@Component
public class AuthInterceptor {

    @Resource
    private RedisComponent redisComponent;

    @Before("@annotation(com.tzx.chat.annotation.AuthCheck)")
    public void intercept(JoinPoint pjp){
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        AuthCheck authCheck = method.getAnnotation(AuthCheck.class);
        if (authCheck == null){
            return;
        }
        if (authCheck.checkLogin() || authCheck.checkAdmin()){
            checkLogin(authCheck.checkAdmin());
        }

    }
    private void checkLogin(Boolean checkAdmin){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String token = request.getHeader("token");
        if (token == null || token.isEmpty()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LoginUserVO loginUserVO = redisComponent.getTokenUserIndo(token);
        //校验登录
        log.info("用户数据：{}",loginUserVO);
        if (loginUserVO == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //todo 校验管理员

    }




}
