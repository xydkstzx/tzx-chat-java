package com.tzx.chat.entiy.request.fridens;

import lombok.Data;

// 发送好友请求参数
@Data
public class AddFriendRequest {

    private String friendId;    // 被添加方用户ID（必填）
    private String remark;    // 备注名（可选）
}