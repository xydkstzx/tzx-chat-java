package com.tzx.chat.controller;


import com.tzx.chat.annotation.AuthCheck;
import com.tzx.chat.common.BaseResponse;
import com.tzx.chat.common.ResultUtils;
import com.tzx.chat.component.RedisComponent;
import com.tzx.chat.entiy.enums.ErrorCode;
import com.tzx.chat.entiy.request.user.UserLoginRequest;
import com.tzx.chat.entiy.request.user.UserRegisterRequest;
import com.tzx.chat.entiy.vo.user.LoginUserVO;
import com.tzx.chat.exception.BusinessException;
import com.tzx.chat.service.UserInfoService;
import com.tzx.chat.websocket.WebSocketService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping("/account")
public class AccountController {
    @Resource
    private RedisComponent redisComponent;
    @Resource
    private UserInfoService userInfoService;
    @Resource
    private WebSocketService webSocketService;

    /**
     * 用户注册
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<?> register(@Valid @RequestBody UserRegisterRequest userRegisterRequest){
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userName = userRegisterRequest.getUserName();
        String password = userRegisterRequest.getPassword();
        String nickName = userRegisterRequest.getNickName();
        if (StringUtils.isAnyBlank(userName, password, nickName)) {
            return null;
        }
        String result = userInfoService.userRegister(userName, password, nickName);
        return ResultUtils.success(result);
    }
    /**
     * 用户登录
     * @param userLoginRequest
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userName = userLoginRequest.getUserName();
        String password = userLoginRequest.getPassword();
        if (StringUtils.isAnyBlank(userName, password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LoginUserVO loginUserVO = userInfoService.userLogin(userName, password);
        return ResultUtils.success(loginUserVO);
    }

    //@AuthCheck
    @PostMapping("/getUserInfoVO")
    public BaseResponse<LoginUserVO> getUserInfoVO(HttpServletRequest request){
        LoginUserVO loginUserVO = userInfoService.getTokenUserVO(request);
        return ResultUtils.success(loginUserVO);
    }

    @AuthCheck
    @PostMapping(value = "/logout")
    public BaseResponse<Boolean> logout(HttpServletRequest request) {
        String token = request.getHeader("token");
        LoginUserVO tokenUserIndo = redisComponent.getTokenUserIndo(token);
        redisComponent.cleanTokenInfo(token);
        redisComponent.cleanTokenUserId(tokenUserIndo.getId());
        //todo 退出ws连接

        return ResultUtils.success(true);
    }





}
