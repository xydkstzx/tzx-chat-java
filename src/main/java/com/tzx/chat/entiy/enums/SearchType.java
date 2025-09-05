package com.tzx.chat.entiy.enums;

import lombok.Data;
import lombok.Getter;

/**
 * 搜索类型枚举
 */
@Getter
public enum SearchType {
    FRIEND("friend", "好友信息"),
    GROUP("group", "群组信息"),
    CHAT("chat", "聊天信息");

    private final String code;
    private final String desc;

    SearchType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    // 根据code获取枚举
    public static SearchType getByCode(String code) {
        for (SearchType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }

    // getter方法
    public String getCode() { return code; }
}