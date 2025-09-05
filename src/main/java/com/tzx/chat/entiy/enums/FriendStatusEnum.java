package com.tzx.chat.entiy.enums;

import lombok.Getter;

/**
 * 好友关系状态枚举类
 * 对应 friend 表中的 status 字段
 */
@Getter
public enum FriendStatusEnum {

    /**
     * 待验证状态
     */
    PENDING_VERIFICATION(0, "待验证"),

    /**
     * 已成为好友状态
     */
    ALREADY_FRIENDS(1, "已成为好友"),

    /**
     * 已删除状态
     */
    DELETED(2, "已拉黑");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 状态描述
     */
    private final String description;

    /**
     * 构造方法
     *
     * @param code        状态码
     * @param description 状态描述
     */
    FriendStatusEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据状态码获取对应的枚举实例
     *
     * @param code 状态码
     * @return 对应的枚举实例，如果没有匹配的则返回 null
     */
    public static FriendStatusEnum getByCode(int code) {
        for (FriendStatusEnum status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}