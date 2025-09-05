package com.tzx.chat.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tzx.chat.component.RedisComponent;
import com.tzx.chat.entiy.domain.UserInfo;
import com.tzx.chat.entiy.enums.ErrorCode;
import com.tzx.chat.entiy.enums.MessageCode;
import com.tzx.chat.entiy.vo.user.LoginUserVO;
import com.tzx.chat.exception.BusinessException;
import com.tzx.chat.service.UserInfoService;
import com.tzx.chat.mapper.UserInfoMapper;
import com.tzx.chat.util.JwtUtil;
import com.tzx.chat.websocket.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
* @author Administrator
* @description 针对表【user_info(用户表)】的数据库操作Service实现
* @createDate 2025-08-29 22:10:22
*/
@Slf4j
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {
    @Resource
    private RedisComponent redisComponent;
    @Resource
    private WebSocketService webSocketService;


    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "tzx";
    /**
     * 用户注册
     * @param userName
     * @param password
     * @param nickName
     * @return
     */
    @Override
    public String userRegister(String userName, String password, String nickName) {
        synchronized (UserInfoService.class) {
            //账号密码不能重复
            LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(UserInfo::getUserName, userName);
            UserInfo userInfo = getOne(queryWrapper);
            if (userInfo != null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已被使用");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
            // 3. 插入数据
            UserInfo user = new UserInfo();
            user.setUserName(userName);
            user.setPassword(encryptPassword);
            user.setNickName(nickName);
            boolean save = save(user);
            if (!save) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    /**
     * 登录
     * @param userName
     * @param password
     * @return
     */
    @Override
    public LoginUserVO userLogin(String userName, String password) {
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfo::getUserName, userName).eq(UserInfo::getPassword, encryptPassword);
        UserInfo userInfo = this.getOne(queryWrapper);
        if (userInfo == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        //当用户a已经登录时，token 已经存入到redis  我在用户b登录，通过用户id获取的token就是旧token
        String userId = userInfo.getId(); // 获取用户ID
        webSocketService.closeOldConnection(userId, MessageCode.LOGIN_OTHER_DEVICE.getCode());
        LoginUserVO loginUserVO = getLoginUserVO(userInfo);
        return loginUserVO;
    }

    @Override
    public LoginUserVO getTokenUserVO(HttpServletRequest request) {
        LoginUserVO token = redisComponent.getTokenUserIndo(request.getHeader("token"));
        if (token == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return token;
    }

    public LoginUserVO getLoginUserVO(UserInfo userInfo) {
        if (userInfo == null) {
            return null;
        }
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(userInfo, loginUserVO);
        //生成token
        JSONObject jsonObject = (JSONObject) JSONObject.toJSON(loginUserVO);
        String token = JwtUtil.createToken(jsonObject);
        loginUserVO.setToken(token);
        //把数据存入redis
        redisComponent.saveLoginUserVO(loginUserVO);
        return loginUserVO;
    }



}





