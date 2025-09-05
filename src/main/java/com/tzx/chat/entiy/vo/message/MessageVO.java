package com.tzx.chat.entiy.vo.message;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.Date;

/**
 * 聊天消息数据传输对象（DTO）
 * 用于前端渲染聊天窗口，封装消息核心信息（含发送者详情、格式化内容等）
 */
@Data
@ApiModel(description = "聊天消息DTO（前端渲染用）")
public class MessageVO {

    /**
     * 消息唯一ID
     * （对应Message实体的id，用于消息定位、撤回等操作）
     */

    private String id;
    /**
     * 发送者用户ID
     * （对应Message实体的from_id，用于前端判断“是否是自己发送的消息”）
     */
    private String fromId;
    /**
     * 发送者昵称
     * （从User表查询补充，用于前端显示发送者名称）
     */
    private String fromNickname;
    /**
     * 发送者头像URL
     * （从User表查询补充，用于前端显示发送者头像）
     */
    private String fromAvatar;
    /**
     * 接收者用户ID
     * （对应Message实体的target_id，可选：前端需区分消息接收方时使用）
     */
    private String targetId;
    /**
     * 消息类型
     * （1-文本，2-图片，3-文件，4-视频，5-表情包，与Message实体的msg_type一致）
     */
    private Integer msgType;

    /**
     * 格式化后的消息内容
     * （如图片显示[图片]、文件显示[文件：xxx.pdf]，用于前端快速预览）
     */
    private String content;

    /**
     * 原始消息内容
     * （存储未格式化的原始数据，如图片URL、文件下载地址，用于前端点击查看/下载）
     */
    private String originalContent;

    /**
     * 文件名称
     * （仅消息类型为3-文件/4-视频时有效，对应Message实体的file_name）
     */
    private String fileName;

    /**
     * 消息发送时间
     * （对应Message实体的send_time，用于前端显示消息时间戳）
     */
    private Date sendTime;

    /**
     * 消息状态
     * （1-已发送，2-已读，与Message实体的status一致，用于前端显示已读/未读标识）
     */
    private Integer status;
}