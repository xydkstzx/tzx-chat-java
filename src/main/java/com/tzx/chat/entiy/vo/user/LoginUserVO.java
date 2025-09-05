package com.tzx.chat.entiy.vo.user;

import lombok.Data;

@Data
public class LoginUserVO {
    /**
     * id
     */
    private String id;
    /**
     * 账号
     */
    private String userName;
    /**
     * 昵称（显示用）
     */
    private String nickName;
    /**
     * 头像URL
     */
    private String avatar;

    /**
     * 性别（0-未知，1-男，2-女）
     */
    private Integer gender;

    /**
     * 个性签名
     */
    private String signature;

    /**
     * 状态（1-在线，2-离线，3-隐身）
     */
    private Integer status;
    /**
     * token
     */
    private String token;



}
