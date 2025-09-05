package com.tzx.chat.entiy.enums;

import lombok.Getter;

/**
 * 会话接收类型枚举
 * 对应 conversation 表的 target_type 字段（tinyint 类型）
 */
@Getter
public enum TargetTypeEnum {

    /**
     * 私聊：接收类型为“一对一聊天”，target_id 关联好友的 user_id
     */
    PRIVATE_CHAT(1, "私聊"),

    /**
     * 群聊：接收类型为“多人群聊”，target_id 关联群聊的 group_id
     */
    GROUP_CHAT(2, "群聊");

    /**
     * 数据库存储的码值（对应 target_type 字段的 tinyint 值）
     */
    private final int code;

    /**
     * 业务描述（对应字段注释的说明）
     */
    private final String desc;

    /**
     * 私有构造：枚举类构造方法必须私有，避免外部实例化
     * @param code 数据库码值
     * @param desc 业务描述
     */
    TargetTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据数据库码值获取对应的枚举实例（业务层常用：从数据库查回 code 后转枚举）
     * @param code 数据库存储的 target_type 码值（1/2）
     * @return 对应的枚举实例，若码值无效则抛出 IllegalArgumentException
     */
    public static TargetTypeEnum of(int code) {
        for (TargetTypeEnum typeEnum : values()) {
            if (typeEnum.getCode() == code) {
                return typeEnum;
            }
        }
        throw new IllegalArgumentException("无效的会话接收类型码值：" + code + "，允许的值为 1（私聊）、2（群聊）");
    }

    /**
     * 重写 toString：方便日志打印时展示“码值+描述”，而非默认的枚举名
     * @return 格式化字符串（如："TargetTypeEnum{code=1, desc='私聊'}"）
     */
    @Override
    public String toString() {
        return "TargetTypeEnum{" +
                "code=" + code +
                ", desc='" + desc + '\'' +
                '}';
    }
}