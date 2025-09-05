package com.tzx.chat.constants;

public class Constants {
    public static final String MSG = "message";
    //添加用户通知
    public static final String ADD_FRIEND = "addFriend";
    //默认信息添加
    public static final String DEFAULT_USER = "defaultUser";
    //私聊消息
    public static final String PRIVATE_CHEAT_MESSAGE = "privateChatMessage";
    public static final String FILE_FOLDER = "file/";
    public static final String FILE_AVATAR = "avatar/";
    public static final Integer LENGTH_30 = 30;

    private static final String REDIS_KEY_PREFIX = "tzxchat:";
    public static final String USER_ID_TO_TOKEN_KEY =REDIS_KEY_PREFIX + "userIdToToken:";
    public static final String REDIS_KEY_WS_USERID = REDIS_KEY_PREFIX +"ws:userid:";
    public static final String REDIS_KEY_WS_TOKEN =REDIS_KEY_PREFIX+ "ws:token:";
    public static final String REDIS_KEY_OFFLINE_MES = REDIS_KEY_PREFIX + "offlineMes:";
    public static final Integer REDIS_KEY_EXPIRES_ONE_MIN = 60000;
    public static final Integer REDIS_KEY_EXPIRES_DAY = REDIS_KEY_EXPIRES_ONE_MIN * 60 * 24;

}
