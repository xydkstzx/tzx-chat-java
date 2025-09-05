package com.tzx.chat.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tzx.chat.entiy.domain.Message;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tzx.chat.entiy.request.message.SendMessageRequest;
import com.tzx.chat.entiy.vo.message.MessageVO;

/**
* @author Administrator
* @description 针对表【message(消息表)】的数据库操作Service
* @createDate 2025-09-01 11:08:42
*/
public interface MessageService extends IService<Message> {

    Page<MessageVO> getPrivateChatRecords(String currentUserId, String targetUserId, long current, long pageSize);

    /**
     * 发送信息
     * @param currentUserId
     * @param sendMessageRequest
     * @return
     */
    MessageVO send(String currentUserId, SendMessageRequest sendMessageRequest);

    /**
     * 更新消息用户状态
     * @param messageId
     * @param status
     */
    void updateMessageStatus(String messageId, Integer status);
}
