package com.tzx.chat.entiy.vo.fridens;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

// 待处理好友请求的VO（返回给前端）
@Data
public class FriendRequestVO {
    // 关键：添加 friendRelationId，对应 friend 表的主键 id
    private String friendRelationId;  // 这就是后续处理请求时需要的 relationId
    private String requesterId;       // 发起请求的用户ID（比如用户A的ID）
    private String requesterName;   // 发起请求的用户账号（从user表查询）
    private String requesterAvatar;
    private String requestNickName;// 发起请求的用户头像（可选）
    private Date createTime; // 请求发送时间
}