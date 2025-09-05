package com.tzx.chat.entiy.vo.conversations;

import lombok.Data;

import java.util.Date;

@Data
public class ConversationVO {
    // 会话ID
    private String id;
    // 目标类型（1-私聊，2-群聊）
    private Integer targetType;
    // 目标ID（私聊-对方用户ID，群聊-群ID）
    private String targetId;
    // 目标名称（私聊-对方昵称，群聊-群名称）
    private String targetName;
    // 目标头像（私聊-对方头像，群聊-群头像）
    private String targetAvatar;
    // 最后一条消息内容
    private String lastMsgContent;
    // 最后一条消息发送时间
    private Date lastMsgTime;
    // 未读消息数
    private Integer unreadCount;
    // 是否置顶（0-否，1-是）
    private Integer isTop;
}