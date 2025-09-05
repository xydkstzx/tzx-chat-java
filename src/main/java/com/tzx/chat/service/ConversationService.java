package com.tzx.chat.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tzx.chat.entiy.domain.Conversation;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tzx.chat.entiy.vo.conversations.ConversationVO;

/**
* @author Administrator
* @description 针对表【conversation(会话表)】的数据库操作Service
* @createDate 2025-09-01 11:08:36
*/
public interface ConversationService extends IService<Conversation> {

    Page<ConversationVO> listConversations(String id, long current, long pageSize);

    ConversationVO getConversationDetail(String id, String conversationId);

    void updateConversation(String currentUserId, String targetUserId, String id, int i);
}
