package com.tzx.chat.entiy.enums;

/**
 * 消息状态枚举
 */
public enum MessageStatusEnum {
    SENDING(0, "发送中"),
    SENT(1, "已发送"),
    DELIVERED(2, "已送达"),
    READ(3, "已读"),
    FAILED(4, "发送失败");

    private final int code;
    private final String desc;

    MessageStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    // 根据code获取枚举
    public static MessageStatusEnum getByCode(int code) {
        for (MessageStatusEnum status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
    