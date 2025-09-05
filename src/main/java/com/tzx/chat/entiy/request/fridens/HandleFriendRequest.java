package com.tzx.chat.entiy.request.fridens;

import lombok.Data;

// 处理好友请求参数
@Data
public class HandleFriendRequest {

    private String friendRelationId;// 好友关系ID（必填）
    private Integer status;         // 处理结果（1-同意，2-拒绝）
}