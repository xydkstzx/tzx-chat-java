package com.tzx.chat.entiy.enums;

public enum MessageCode {

    LOGIN_OTHER_DEVICE("1004", "您的账号已在其他设备登录，被迫下线"),
    // WebSocket连接相关消息
    WS_CONNECT_SUCCESS("2001", "WebSocket连接成功"),
    WS_CONNECT_FAIL("2002", "WebSocket连接失败");
//    WS_DISCONNECT(2003, "WebSocket连接已断开"),
//    WS_HEARTBEAT(2004, "心跳检测"),
//    WS_TOKEN_INVALID(2005, "token无效，拒绝连接"),
//    // 业务消息
//    BUSINESS_NORMAL(3001, "普通业务消息"),
//    BUSINESS_NOTIFY(3002, "业务通知"),
//    BUSINESS_ALERT(3003, "业务预警"),
//    // 好友/聊天相关（如果有社交场景）
//    FRIEND_ONLINE(4001, "好友上线通知"),
//    FRIEND_OFFLINE(4002, "好友下线通知"),
//    NEW_MESSAGE(4003, "收到新消息");
    /**
     * 消息编码
     */
    private final String code;

    /**
     * 消息描述
     */
    private final String desc;

    MessageCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 根据编码获取枚举
     */
    public static MessageCode getByCode(String code) {
        for (MessageCode messageCode : values()) {
            if (messageCode.code .equalsIgnoreCase(code)) {
                return messageCode;
            }
        }
        return null;
    }

}
