package com.tzx.chat.entiy.request.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserUpdateMyRequest implements Serializable {

    /**
     * 用户昵称
     */
    private String nickName;
    /**
     * 用户头像
     */
    private String avatar;
    /**
     * 简介
     */
    private String signature;
    /**
     * 密码
     */
    private String password;
    /**
     * 性别
     */
    private Integer gender;



    private static final long serialVersionUID = 1L;
}