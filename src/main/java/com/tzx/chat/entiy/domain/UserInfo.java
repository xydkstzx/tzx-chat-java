package com.tzx.chat.entiy.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * 用户表
 * @TableName user_info
 */
@TableName(value ="user_info")
@Data
public class UserInfo {
    /**
     * 用户ID（主键）
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 用户名（登录用）
     */
    private String userName;

    /**
     * 密码（加密存储）
     */
    private String password;

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
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createdTime;
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedTime;

    @TableField(exist = false)
    private boolean IsFriend;

}