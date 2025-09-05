package com.tzx.chat.controller;

import com.tzx.chat.annotation.AuthCheck;
import com.tzx.chat.common.BaseResponse;
import com.tzx.chat.common.ResultUtils;
import com.tzx.chat.entiy.domain.UserInfo;
import com.tzx.chat.entiy.enums.ErrorCode;
import com.tzx.chat.entiy.request.user.UserUpdateMyRequest;
import com.tzx.chat.entiy.vo.user.LoginUserVO;
import com.tzx.chat.exception.BusinessException;
import com.tzx.chat.service.UserInfoService;
import com.tzx.chat.util.CopyTools;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/userInfo")
public class UserInfoController {
    @Resource
    private UserInfoService userInfoService;


    /**
     * 修改用户信息
     */
    @AuthCheck
    @PostMapping("/update")
    public BaseResponse<LoginUserVO> updateUserInfo(@RequestBody UserUpdateMyRequest userUpdateMyRequest, HttpServletRequest request) {
        // 1. 获取当前登录用户ID
        String currentUserId = userInfoService.getTokenUserVO(request).getId();
        if (currentUserId == null || currentUserId.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"获取当前用户信息失败");
        }
        // 2. 验证用户是否存在
        UserInfo existingUser = userInfoService.getById(currentUserId);
        if (existingUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户不存在");
        }
        // 3. 构建更新对象
        UserInfo updateUser = new UserInfo();
        updateUser.setId(currentUserId);
        // 4. 设置需要更新的字段
        if (userUpdateMyRequest.getNickName() != null) {
            updateUser.setNickName(userUpdateMyRequest.getNickName());
        }
        if (userUpdateMyRequest.getAvatar() != null) {
            updateUser.setAvatar(userUpdateMyRequest.getAvatar());
        }
        if (userUpdateMyRequest.getSignature() != null) {
            updateUser.setSignature(userUpdateMyRequest.getSignature());
        }
        if (userUpdateMyRequest.getGender() != null) {
            // 验证性别值是否合法（0-未知，1-男，2-女）
            if (userUpdateMyRequest.getGender() < 0 || userUpdateMyRequest.getGender() > 2) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"性别参数不合法");
            }
            updateUser.setGender(userUpdateMyRequest.getGender());
        }
        // 5. 处理密码更新（单独处理，需要加密）
        if (userUpdateMyRequest.getPassword() != null && !userUpdateMyRequest.getPassword().isEmpty()) {
            // 这里假设你有密码加密工具类，实际项目中请使用合适的加密方式
            String encryptPassword = DigestUtils.md5DigestAsHex(("tzx" + userUpdateMyRequest.getPassword()).getBytes());
            updateUser.setPassword(encryptPassword);
        }
        // 6. 执行更新操作
        boolean updateSuccess = userInfoService.updateById(updateUser);
        // 7. 返回结果
        if (updateSuccess) {
            // 更新成功后返回更新后的用户信息（注意脱敏，不返回密码）
            UserInfo updatedUser = userInfoService.getById(currentUserId);
            LoginUserVO loginUserVO = CopyTools.copy(updatedUser, LoginUserVO.class);
            return ResultUtils.success(loginUserVO);
        } else {
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR);
        }
    }






}
