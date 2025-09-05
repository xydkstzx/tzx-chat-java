package com.tzx.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tzx.chat.entiy.domain.Conversation;
import com.tzx.chat.entiy.domain.Message;
import com.tzx.chat.entiy.domain.UserInfo;
import com.tzx.chat.entiy.enums.ErrorCode;
import com.tzx.chat.entiy.enums.TargetTypeEnum;
import com.tzx.chat.entiy.vo.conversations.ConversationVO;
import com.tzx.chat.exception.BusinessException;
import com.tzx.chat.mapper.MessageMapper;
import com.tzx.chat.mapper.UserInfoMapper;
import com.tzx.chat.service.ConversationService;
import com.tzx.chat.mapper.ConversationMapper;
import com.tzx.chat.service.MessageService;
import com.tzx.chat.service.UserInfoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【conversation(会话表)】的数据库操作Service实现
* @createDate 2025-09-01 11:08:36
*/
@Service
public class ConversationServiceImpl extends ServiceImpl<ConversationMapper, Conversation> implements ConversationService{
    @Resource
    private UserInfoService userInfoService;
    @Resource
    private MessageMapper messageMapper;


    /**
     * 分页查询会话列表
     * @param userId
     * @param current
     * @param pageSize
     * @return
     */
    @Override
    public Page<ConversationVO> listConversations(String userId, long current, long pageSize) {
        // 1. 分页查询当前用户的会话记录（按更新时间倒序，最新会话在前）
        Page<Conversation> conversationPage = new Page<>(current, pageSize);
        LambdaQueryWrapper<Conversation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Conversation::getUserId, userId).orderByAsc(Conversation::getUpdatedTime);
        Page<Conversation> pageResult = page(conversationPage, queryWrapper);
        // 2. 转换为VO并补充关联信息
        Page<ConversationVO> resultPage = new Page<>(current, pageSize);
        resultPage.setTotal(pageResult.getTotal());
        List<ConversationVO> dtoList = pageResult.getRecords().stream().map(conversation -> {
            ConversationVO dto = new ConversationVO();
            BeanUtils.copyProperties(conversation, dto);
            // 3. 补充目标信息（用户/群聊的名称和头像）
            if (conversation.getTargetType() == 1) { // 私聊
                UserInfo userInfo = userInfoService.getById(conversation.getTargetId());
                dto.setTargetName(userInfo.getNickName());
                dto.setTargetAvatar(userInfo.getAvatar());
            } else if (conversation.getTargetType() == 2) { // 群聊

            }
            // 4. 补充最后一条消息的内容和时间
            if (conversation.getLastMsgId() != null) {
                Message lastMsg = messageMapper.selectById(conversation.getLastMsgId());
                if (lastMsg != null) {
                    dto.setLastMsgContent(lastMsg.getContent()); // 处理消息内容（如截断长文本）
                    dto.setLastMsgTime(lastMsg.getSendTime());
                }
            }
            return dto;
        }).collect(Collectors.toList());
        resultPage.setRecords(dtoList);
        return resultPage;
    }


    /**
     *
     * @param userId 登录用户的id
     * @param conversationId
     * @return
     */
    @Override
    public ConversationVO getConversationDetail(String userId, String conversationId) {
        Conversation conversation = getById(conversationId);
        //权限校验
        if (conversation == null || !conversation.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "会话不存在或无权限");
        }
        // 转换为DTO（逻辑同列表查询，补充目标信息和最后一条消息）
        ConversationVO vo = new ConversationVO();
        BeanUtils.copyProperties(conversation, vo);
        // 1. 补充目标信息（用户/群聊的名称和头像）
        Integer targetType = conversation.getTargetType();
        String targetId = conversation.getTargetId();
        if (targetType == 1) { // 私聊：查询对方用户信息
            UserInfo userInfo = userInfoService.getById(targetId);
            if (userInfo != null) {
                vo.setTargetName(userInfo.getNickName());
                vo.setTargetAvatar(userInfo.getAvatar());
            }
        } else if (targetType == 2) { // 群聊：查询群聊信息
//            Group group = groupService.getById(targetId);
//            if (group != null) {
//                vo.setTargetName(group.getGroupName());
//                vo.setTargetAvatar(group.getGroupAvatar());
//                // 可选：补充群成员数量
//                vo.setTargetMemberCount(groupMemberService.countByGroupId(targetId));
//            }
        }
        // 2. 补充最后一条消息的内容和时间
        String lastMsgId = conversation.getLastMsgId();
        if (lastMsgId != null) {
            Message lastMsg = messageMapper.selectById(lastMsgId);
            if (lastMsg != null) {
                // 处理消息内容（根据类型显示不同格式）
                vo.setLastMsgContent(handleMsgContent(lastMsg));
                vo.setLastMsgTime(lastMsg.getSendTime());
            }
        }

        // 可选：查询会话后将未读消息标记为已读
        if (conversation.getUnreadCount() > 0) {
            conversation.setUnreadCount(0);
            updateById(conversation);
        }
        return vo;
    }

    /**
     * 更新会话信息
     * @param userId 当前用户ID
     * @param targetId 目标用户ID
     * @param lastMsgId 最后一条消息ID
     * @param unreadCount 新增未读数量
     */
    @Override
    public void updateConversation(String userId, String targetId, String lastMsgId, int unreadCount) {
        //查询当前用户已私聊用户的会话信息
        LambdaQueryWrapper<Conversation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Conversation::getUserId, userId)
                .eq(Conversation::getTargetId, targetId)
                .eq(Conversation::getTargetType, TargetTypeEnum.PRIVATE_CHAT.getCode());

        Conversation conversation = getOne(queryWrapper);
        if (conversation == null) {
            // 会话不存在，创建新会话
            conversation = new Conversation();
            conversation.setUserId(userId);
            conversation.setTargetId(targetId);
            conversation.setTargetType(TargetTypeEnum.PRIVATE_CHAT.getCode());
            conversation.setUnreadCount(unreadCount);
            conversation.setIsTop(0);
            conversation.setIsMute(0);
            conversation.setCreatedTime(new Date());
        } else {
            // 会话存在，更新未读计数
            conversation.setUnreadCount(conversation.getUnreadCount() + unreadCount);
        }
        // 更新最后一条消息ID和时间
        conversation.setLastMsgId(lastMsgId);
        conversation.setUpdatedTime(new Date());
        saveOrUpdate(conversation);
    }

    /**
     * 处理消息内容（根据类型显示不同格式）
     */
    private String handleMsgContent(Message message) {
        switch (message.getMsgType()) {
            case 1: // 文本
                return message.getContent();
            case 2: // 图片
                return "[图片]";
            case 3: // 文件
                return "[文件：" + message.getFileName() + "]";
            case 4: // 视频
                return "[视频]";
            case 5: // 表情包
                return "[表情]";
            default:
                return "[未知消息]";
        }
    }
}




