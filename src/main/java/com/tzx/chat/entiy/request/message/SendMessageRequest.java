package com.tzx.chat.entiy.request.message;

import com.tzx.chat.constants.Constants;
import lombok.Data;

import java.awt.*;

@Data
public class SendMessageRequest {
    //接收者id
    private String targetUserId;
    //1,表示文本  其他数字表其他信息类型，
    private Integer type;
    //聊天内容信息
    private String messageContent;
    //文件地址
    private Long fileSize;

    private String fileName;




}
