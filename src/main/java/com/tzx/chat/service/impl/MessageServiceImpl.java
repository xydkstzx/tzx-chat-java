package com.tzx.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tzx.chat.component.RedisComponent;
import com.tzx.chat.constants.Constants;
import com.tzx.chat.entiy.domain.Message;
import com.tzx.chat.entiy.domain.UserInfo;
import com.tzx.chat.entiy.enums.ErrorCode;
import com.tzx.chat.entiy.enums.MessageStatusEnum;
import com.tzx.chat.entiy.enums.MessageTypeEnum;
import com.tzx.chat.entiy.enums.TargetTypeEnum;
import com.tzx.chat.entiy.request.message.SendMessageRequest;
import com.tzx.chat.entiy.vo.message.MessageVO;
import com.tzx.chat.exception.BusinessException;
import com.tzx.chat.service.ConversationService;
import com.tzx.chat.service.MessageService;
import com.tzx.chat.mapper.MessageMapper;
import com.tzx.chat.service.UserInfoService;
import com.tzx.chat.websocket.WebSocketService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【message(消息表)】的数据库操作Service实现
* @createDate 2025-09-01 11:08:42
*/
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService{

    @Resource
    private UserInfoService userInfoService;
    @Resource
    private WebSocketService webSocketService;
    @Resource
    private RedisComponent redisComponent;
    @Resource
    private ThreadPoolTaskExecutor taskExecutor;
    @Resource
    private ConversationService conversationService;

    /**
     * 查询当前两个消息的聊天记录
     * @param currentUserId 发送者id
     * @param targetUserId 接收者id
     * @param current
     * @param pageSize
     * @return
     */
    @Override
    public Page<MessageVO> getPrivateChatRecords(String currentUserId, String targetUserId, long current, long pageSize) {
        Page<Message> page = new Page<>(current, pageSize);
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        //第一个条件组：查询当前哟呼发送目标用户的所有信息
        queryWrapper.nested(qw -> qw
                .eq(Message::getFromId,currentUserId)
                .eq(Message::getTargetId,targetUserId)
        ).or()
                //第二个条件组查询目标用户发送给当前用户的所有信息
                .nested(qw -> qw
                .eq(Message::getTargetId,currentUserId)
                .eq(Message::getFromId,targetUserId));
        //按照发送时间排序
        queryWrapper.orderByAsc(Message::getSendTime);
        //关联查询信息
        Page<Message> messagePage = page(page, queryWrapper);
        //转换为VO实体类，补充发送者信息头像
        Page<MessageVO> voPage = new Page<>(current, pageSize);
        voPage.setTotal(messagePage.getTotal());
        voPage.setRecords(messagePage.getRecords().stream().map(message -> {
            MessageVO vo = new MessageVO();
            // 复制基本字段
            BeanUtils.copyProperties(message, vo);
            // 补充发送者信息（需要调用用户服务查询昵称、头像）
            UserInfo sender = userInfoService.getById(message.getFromId());
            if (sender != null) {
                vo.setFromNickname(sender.getNickName());
                vo.setFromAvatar(sender.getAvatar());
            }
            // 格式化消息内容（如[图片]、[文件]）
            vo.setContent(message.getContent());
            return vo;
        }).collect(Collectors.toList()));
        return voPage;
    }

    /**
     * 发送私聊信息
     * @param currentUserId
     * @param sendMessageRequest
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageVO send(String currentUserId, SendMessageRequest sendMessageRequest) {
        //校验参数类型
        validateSendMessageParams(sendMessageRequest);
        //构建消息实体
        Message message = buildMessage(currentUserId, sendMessageRequest);
        // 4. 保存消息到数据库（核心操作，确保消息不丢失）
        boolean saveResult = save(message);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "消息发送失败");
        }
        //补充参数数据
        MessageVO messageVO = convertToMessageVO(message);
        // 6. 异步处理发送信息 更改会话  更改消息状态
        CompletableFuture.runAsync(() -> processMessageAsync(currentUserId, sendMessageRequest, message, messageVO),
                        taskExecutor)
                .exceptionally(ex -> {
                    log.error("消息异步处理失败", ex);
                    // 记录失败日志，可考虑添加补偿机制
                    return null;
                });
        return messageVO;
    }
    /**
     * 构建消息实体
     * @param currentUserId 当前扽牢固id
     * @param request 发送消息内容
     * @return
     */
    private Message buildMessage(String currentUserId, SendMessageRequest request) {
        Message message = new Message();
        message.setFromId(currentUserId);
        message.setTargetId(request.getTargetUserId());
        message.setTargetType(TargetTypeEnum.PRIVATE_CHAT.getCode());
        message.setSendTime(new Date());
        message.setStatus(MessageStatusEnum.SENDING.getCode()); // 初始状态为发送中
        // 根据消息类型设置不同内容
        if (request.getType().equals(MessageTypeEnum.FILE.getCode())) {
            message.setMsgType(MessageTypeEnum.FILE.getCode());
            message.setContent(request.getMessageContent());
            message.setFileName(request.getFileName());
            message.setFileSize(request.getFileSize());
        } else if (request.getType().equals(MessageTypeEnum.IMAGE.getCode())) {
            message.setMsgType(MessageTypeEnum.IMAGE.getCode());
            message.setContent(request.getMessageContent());
            message.setFileSize(request.getFileSize());
        } else {
            message.setMsgType(MessageTypeEnum.TEXT.getCode());
            message.setContent(request.getMessageContent());
        }
        return message;
    }
    /**
     * 验证发送消息参数
     */
    private void validateSendMessageParams(SendMessageRequest request) {
        if (request == null || StringUtils.isBlank(request.getTargetUserId()) ||
                (request.getType().equals(MessageTypeEnum.TEXT.getCode()) &&
                        StringUtils.isBlank(request.getMessageContent())) ||
                (!request.getType().equals(MessageTypeEnum.TEXT.getCode() )&&
                        StringUtils.isBlank(request.getMessageContent()))) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "消息参数不完整");
        }

    }

    /**
     * 参数填充
     * @param message
     * @return
     */
    private MessageVO convertToMessageVO(Message message) {
        MessageVO vo = new MessageVO();
        BeanUtils.copyProperties(message, vo);
        // 关联用户表查询发送者信息
        UserInfo userInfo = userInfoService.getById(message.getFromId());
        if (userInfo != null) {
            vo.setFromNickname(userInfo.getNickName());
            vo.setFromAvatar(userInfo.getAvatar());
        }
        return vo;
    }

    /**
     * 异步处理消息后续操作
     */
    private void processMessageAsync(String currentUserId, SendMessageRequest request, Message message, MessageVO messageVO) {
        try {
            // 1.更新会话信息
            //更新当前用户会话信息
            conversationService.updateConversation(currentUserId, request.getTargetUserId(), message.getId(), 0);
            //更新接收用户的会话信息
            conversationService.updateConversation(request.getTargetUserId(), currentUserId, message.getId(), 1);
            // 2. 发送WebSocket消息并更新状态
            boolean isTargetOnline = webSocketService.sendMsgToUser(messageVO, currentUserId, request.getTargetUserId());
            // 3. 更新消息状态
            updateMessageStatus(message.getId(), isTargetOnline ? MessageStatusEnum.DELIVERED.getCode() : MessageStatusEnum.SENT.getCode());
            // 4. 处理离线消息
            if (!isTargetOnline) {
                redisComponent.saveOfflineMessage(request.getTargetUserId(), messageVO);
            }
        } catch (Exception e) {
            log.error("消息异步处理异常", e);
            // 消息状态更新为失败
            updateMessageStatus(message.getId(), MessageStatusEnum.FAILED.getCode());
        }
    }
    /**
     * 更新消息状态
     */
    @Override
    public void updateMessageStatus(String messageId, Integer status) {
        Message updateMsg = new Message();
        updateMsg.setId(messageId);
        updateMsg.setStatus(status);
        if (status == MessageStatusEnum.READ.getCode()) {
            updateMsg.setSendTime(new Date());
        }
        baseMapper.updateById(updateMsg);
    }


}




