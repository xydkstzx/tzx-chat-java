package com.tzx.chat.entiy.enums;

import lombok.Getter;

/**
 * 消息类型枚举
 * 对应数据库message表的msg_type字段
 */
@Getter
public enum MessageTypeEnum {

    /**
     * 文本消息
     */
    TEXT(1, "文本消息"),

    /**
     * 文件消息（如文档、压缩包等）
     */
    FILE(3, "文件消息"),

    /**
     * 视频消息
     */
    VIDEO(4, "视频消息"),

    /**
     * 视频消息
     */
    IMAGE(5, "图片消息"),

    /**
     * 表情包消息
     */
    EMOJI(6, "表情包消息");

    /**
     * 类型编码（存储到数据库的数值）
     */
    private final Integer code;

    /**
     * 类型描述（用于日志、前端显示等）
     */
    private final String desc;

    MessageTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据编码获取对应的枚举
     * @param code 消息类型编码
     * @return 对应的枚举，无匹配时返回null
     */
    public static MessageTypeEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (MessageTypeEnum type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 判断编码是否为有效消息类型
     * @param code 消息类型编码
     * @return true-有效，false-无效
     */
    public static boolean isValidCode(Integer code) {
        return getByCode(code) != null;
    }
}
