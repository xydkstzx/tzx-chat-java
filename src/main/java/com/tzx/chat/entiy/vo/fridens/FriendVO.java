package com.tzx.chat.entiy.vo.fridens;

import lombok.Data;

/**
 * 好友列表展示VO（包含好友基本信息和备注）
 */
@Data
public class FriendVO {
    // 好友的用户ID（对应user表的id）
    private String friendId;
    // 当前用户对该好友的备注名（来自friend表的remark）
    private String remark;
    // 好友的用户名（来自user表）
    private String friendName;
    // 好友的头像URL（来自user表）
    private String avatar;
    //好友昵称
    private String friendNickName;
}