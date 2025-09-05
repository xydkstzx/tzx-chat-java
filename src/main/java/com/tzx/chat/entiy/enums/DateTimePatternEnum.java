package com.tzx.chat.entiy.enums;

import lombok.Getter;

/**
 * 日期时间格式模式枚举类，统一管理项目中使用的日期时间格式
 */
@Getter
public enum DateTimePatternEnum {

    /**
     * 年-月-日 时:分:秒 格式，例如：2023-10-05 14:30:25
     */
    YYYY_MM_DD_HH_MM_SS("yyyy-MM-dd HH:mm:ss"),

    /**
     * 年-月-日 格式，例如：2023-10-05
     */
    YYYY_MM_DD("yyyy-MM-dd"),

    /**
     * 年月日 格式，例如：20231005
     */
    YYYYMMDD("yyyyMMdd"),

    /**
     * 时:分:秒 格式，例如：14:30:25
     */
    HH_MM_SS("HH:mm:ss"),

    /**
     * 年/月/日 格式，例如：2023/10/05
     */
    YYYY_SLASH_MM_SLASH_DD("yyyy/MM/dd"),

    /**
     * 年-月 格式，例如：2023-10
     */
    YYYY_MM("yyyy-MM");

    /**
     * 日期时间格式模式字符串
     */
    private final String pattern;

    /**
     * 构造方法，初始化日期时间格式模式
     *
     * @param pattern 日期时间格式模式字符串
     */
    DateTimePatternEnum(String pattern) {
        this.pattern = pattern;
    }
}
