package com.tzx.chat.entiy.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import lombok.Data;

/**
 * 会话表
 * @TableName conversation
 */
@TableName(value ="conversation")
@Data
public class Conversation {
    /**
     * 会话ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 所属用户ID（关联user表id）
     */
    private String userId;

    /**
     * 会话类型（1-私聊，2-群聊）
     */
    private Integer targetType;

    /**
     * 目标ID（私聊-好友user_id，群聊-group_id）
     */
    private String targetId;

    /**
     * 最后一条消息ID（关联message表id）
     */
    private String lastMsgId;

    /**
     * 未读消息数
     */
    private Integer unreadCount;

    /**
     * 是否置顶（0-否，1-是）
     */
    private Integer isTop;

    /**
     * 是否静音（0-否，1-是）
     */
    private Integer isMute;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createdTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedTime;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        Conversation other = (Conversation) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getTargetType() == null ? other.getTargetType() == null : this.getTargetType().equals(other.getTargetType()))
            && (this.getTargetId() == null ? other.getTargetId() == null : this.getTargetId().equals(other.getTargetId()))
            && (this.getLastMsgId() == null ? other.getLastMsgId() == null : this.getLastMsgId().equals(other.getLastMsgId()))
            && (this.getUnreadCount() == null ? other.getUnreadCount() == null : this.getUnreadCount().equals(other.getUnreadCount()))
            && (this.getIsTop() == null ? other.getIsTop() == null : this.getIsTop().equals(other.getIsTop()))
            && (this.getIsMute() == null ? other.getIsMute() == null : this.getIsMute().equals(other.getIsMute()))
            && (this.getCreatedTime() == null ? other.getCreatedTime() == null : this.getCreatedTime().equals(other.getCreatedTime()))
            && (this.getUpdatedTime() == null ? other.getUpdatedTime() == null : this.getUpdatedTime().equals(other.getUpdatedTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getTargetType() == null) ? 0 : getTargetType().hashCode());
        result = prime * result + ((getTargetId() == null) ? 0 : getTargetId().hashCode());
        result = prime * result + ((getLastMsgId() == null) ? 0 : getLastMsgId().hashCode());
        result = prime * result + ((getUnreadCount() == null) ? 0 : getUnreadCount().hashCode());
        result = prime * result + ((getIsTop() == null) ? 0 : getIsTop().hashCode());
        result = prime * result + ((getIsMute() == null) ? 0 : getIsMute().hashCode());
        result = prime * result + ((getCreatedTime() == null) ? 0 : getCreatedTime().hashCode());
        result = prime * result + ((getUpdatedTime() == null) ? 0 : getUpdatedTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", userId=").append(userId);
        sb.append(", targetType=").append(targetType);
        sb.append(", targetId=").append(targetId);
        sb.append(", lastMsgId=").append(lastMsgId);
        sb.append(", unreadCount=").append(unreadCount);
        sb.append(", isTop=").append(isTop);
        sb.append(", isMute=").append(isMute);
        sb.append(", createdTime=").append(createdTime);
        sb.append(", updatedTime=").append(updatedTime);
        sb.append("]");
        return sb.toString();
    }
}