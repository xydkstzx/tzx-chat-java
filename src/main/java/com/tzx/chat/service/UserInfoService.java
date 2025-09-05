package com.tzx.chat.service;

import com.tzx.chat.entiy.domain.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tzx.chat.entiy.vo.user.LoginUserVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author Administrator
* @description 针对表【user_info(用户表)】的数据库操作Service
* @createDate 2025-08-29 22:10:22
*/
public interface UserInfoService extends IService<UserInfo> {

    String userRegister(String userName, String password, String nickName);

    LoginUserVO userLogin(String userName, String password);

    LoginUserVO getTokenUserVO(HttpServletRequest request);
}
