package com.tzx.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tzx.chat.entiy.domain.Conversation;
import com.tzx.chat.entiy.domain.Friend;
import com.tzx.chat.entiy.domain.Message;
import com.tzx.chat.entiy.domain.UserInfo;
import com.tzx.chat.entiy.enums.ErrorCode;
import com.tzx.chat.entiy.enums.FriendStatusEnum;
import com.tzx.chat.entiy.enums.TargetTypeEnum;
import com.tzx.chat.entiy.request.fridens.AddFriendRequest;
import com.tzx.chat.entiy.request.fridens.HandleFriendRequest;
import com.tzx.chat.entiy.vo.fridens.FriendRequestVO;
import com.tzx.chat.entiy.vo.fridens.FriendVO;
import com.tzx.chat.entiy.vo.user.LoginUserVO;
import com.tzx.chat.exception.BusinessException;
import com.tzx.chat.service.ConversationService;
import com.tzx.chat.service.FriendService;
import com.tzx.chat.mapper.FriendMapper;
import com.tzx.chat.service.MessageService;
import com.tzx.chat.service.UserInfoService;
import com.tzx.chat.websocket.WebSocketService;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【friend(好友关系表)】的数据库操作Service实现
* @createDate 2025-08-31 10:08:10
*/
@Service
public class FriendServiceImpl extends ServiceImpl<FriendMapper, Friend> implements FriendService{

    // 假设已经注入了线程池
    @Resource
    private ThreadPoolTaskExecutor taskExecutor;

    @Resource
    private UserInfoService userInfoService;
    @Resource
    private WebSocketService webSocketService;
    @Resource
    private ConversationService conversationService;
    @Resource
    private MessageService messageService;

    /**
     * 添加好友
     * @param request
     * @param addFriendRequest 接收申请人id
     * @return
     */
    @Override
    public boolean addFriends(HttpServletRequest request, AddFriendRequest addFriendRequest) {
        //获取当前登录用户信息
        LoginUserVO tokenUserVO = userInfoService.getTokenUserVO(request);
        //发送申请人id
        String applyId = tokenUserVO.getId();
        //接收申请人账号
        String friendUserName = addFriendRequest.getFriendId();
        //通过账号查询被申请人
        LambdaQueryWrapper<UserInfo> userInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userInfoLambdaQueryWrapper.eq(UserInfo::getUserName,friendUserName);
        UserInfo friendUser = userInfoService.getOne(userInfoLambdaQueryWrapper);
        // 1. 校验参数（避免添加自己、重复请求）
        if (applyId.equals(friendUserName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能添加自己为好友");
        }
        //查询是否已经存在好友 或者是像自己发送申请
        LambdaQueryWrapper<Friend> queryWrapper = new LambdaQueryWrapper<Friend>();
        queryWrapper.eq(Friend::getUserId, applyId)
                    .eq(Friend::getFriendId, friendUser.getId())
                    .in(Friend::getStatus, FriendStatusEnum.ALREADY_FRIENDS.getCode(),FriendStatusEnum.PENDING_VERIFICATION.getCode());
        if (baseMapper.exists(queryWrapper)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "已发送好友请求或对方已是您的好友");
        }
        // 3. 创建好友关系记录（状态为待验证）
        Friend friend = new Friend();
        friend.setUserId(applyId);
        friend.setFriendId(friendUser.getId());
        friend.setRemark(addFriendRequest.getRemark());
        friend.setStatus(FriendStatusEnum.PENDING_VERIFICATION.getCode());
        Map<String,Object> map = new HashMap<>();
        map.put("userName", tokenUserVO.getUserName());
        map.put("nickName", tokenUserVO.getNickName());
        map.put("avatar", tokenUserVO.getAvatar());
        //像用户发送好友请求通知
        webSocketService.sendFriendsMsg(map,friendUser.getId());
        return save(friend);
    }

    /**
     * 处理好友请求（多线程优化版）
     * @param request
     * @param handleFriendRequest
     * @return
     */
    @Transactional
    @Override
    public Boolean handleFriendRequest(HttpServletRequest request, HandleFriendRequest handleFriendRequest) {
        LoginUserVO tokenUserVO = userInfoService.getTokenUserVO(request);
        String relationId = handleFriendRequest.getFriendRelationId();
        Integer status = handleFriendRequest.getStatus();

        // 1. 校验状态合法性
        if (!status.equals(FriendStatusEnum.ALREADY_FRIENDS.getCode()) &&
                !status.equals(FriendStatusEnum.DELETED.getCode())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "无效的处理状态");
        }

        // 查询关系记录，确保当前用户是待处理状态
        Friend friend = baseMapper.selectById(relationId);
        if (friend == null || !friend.getFriendId().equals(tokenUserVO.getId()) ||
                !friend.getStatus().equals(FriendStatusEnum.PENDING_VERIFICATION.getCode())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "无效的好友请求");
        }

        // 更新状态
        friend.setStatus(status);

        // 如果是同意成为好友，并行处理相关操作
        if (status.equals(FriendStatusEnum.ALREADY_FRIENDS.getCode())) {
            // 初始化默认消息内容
            String defaultMsgContent = "我们已经成为好友啦，开始聊天吧！";
            String currentUserId = tokenUserVO.getId();
            String targetUserId = friend.getUserId();

            try {
                // 1. 创建初始化聊天信息（作为基础操作，先执行）
                Message initMessage = new Message();
                initMessage.setFromId(currentUserId);
                initMessage.setTargetType(1);  // 1-私聊
                initMessage.setTargetId(targetUserId);
                initMessage.setMsgType(1);  // 1-文本消息
                initMessage.setContent(defaultMsgContent);
                initMessage.setStatus(1);  // 1-已发送
                messageService.save(initMessage);

                // 使用CompletableFuture并行执行后续操作
                // 2. 为当前用户创建会话
                CompletableFuture<Void> currentUserConversationFuture = CompletableFuture.runAsync(() -> {
                    Conversation currentUserConversation = new Conversation();
                    currentUserConversation.setUserId(currentUserId);
                    currentUserConversation.setTargetType(1);
                    currentUserConversation.setTargetId(targetUserId);
                    currentUserConversation.setLastMsgId(initMessage.getId());
                    currentUserConversation.setUnreadCount(0);
                    currentUserConversation.setIsTop(0);
                    currentUserConversation.setIsMute(0);
                    conversationService.save(currentUserConversation);
                }, taskExecutor);

                // 3. 为请求方用户创建会话
                CompletableFuture<Void> targetUserConversationFuture = CompletableFuture.runAsync(() -> {
                    Conversation targetUserConversation = new Conversation();
                    targetUserConversation.setUserId(targetUserId);
                    targetUserConversation.setTargetType(1);
                    targetUserConversation.setTargetId(currentUserId);
                    targetUserConversation.setLastMsgId(initMessage.getId());
                    targetUserConversation.setUnreadCount(1);
                    targetUserConversation.setIsTop(0);
                    targetUserConversation.setIsMute(0);
                    conversationService.save(targetUserConversation);
                }, taskExecutor);

                // 4. 发送WebSocket消息通知对方
                CompletableFuture<Void> webSocketFuture = CompletableFuture.runAsync(() -> {
                    webSocketService.sendUserDefaultMsg(defaultMsgContent, targetUserId);
                }, taskExecutor);

                // 等待所有并行任务完成
                CompletableFuture.allOf(
                        currentUserConversationFuture,
                        targetUserConversationFuture,
                        webSocketFuture
                ).get();  // 阻塞等待所有任务完成

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "处理好友请求被中断");
            } catch (ExecutionException e) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "处理好友请求失败: " + e.getCause().getMessage());
            }
        }

        return updateById(friend);
    }

    /**
     * 查询所有的好友请求数据
     * @param request
     * @return
     */
    @Override
    public List<FriendRequestVO> getPendingRequests(HttpServletRequest request) {
        //被请求方的id
        String userId = userInfoService.getTokenUserVO(request).getId();
        LambdaQueryWrapper<Friend> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Friend::getFriendId, userId).eq(Friend::getStatus, FriendStatusEnum.PENDING_VERIFICATION.getCode());
        List<Friend> requestList = list(queryWrapper);
        //转换为VO包含请求方的数据信息
        List<FriendRequestVO> resultVOList = new ArrayList<>(); // 存储最终VO列表
        // 增强for循环遍历所有待处理请求
        for (Friend friendRequest : requestList) {
            // 2.1 创建VO对象，设置基础字段（从friend表获取）
            FriendRequestVO vo = new FriendRequestVO();
            vo.setFriendRelationId(friendRequest.getId());     // 好友关系ID（处理请求时需要）
            vo.setRequesterId(friendRequest.getUserId());     // 发起方用户ID（friend表的userId）
            vo.setCreateTime(friendRequest.getCreatedTime()); // 请求创建时间
            // 2.2 从用户表查询发起方的详细信息（核心：补充用户名、头像）
            // friendRequest.getUserId() 是发起方的用户ID，用它查user表
            UserInfo requesterUser = userInfoService.getById(friendRequest.getUserId());
            // 防御性处理：避免用户不存在导致空指针（理论上不会出现，除非用户被删除）
            if (requesterUser != null) {
                vo.setRequesterName(requesterUser.getUserName()); // 发起方用户名
                vo.setRequesterAvatar(requesterUser.getAvatar());
                vo.setRequestNickName(requesterUser.getNickName());// 发起方头像（需user表有该字段）
                // 若有其他需要的字段（如用户性别、签名），可继续补充
                // vo.setRequesterGender(requesterUser.getGender());
            } else {
                // 若用户不存在，设置默认值（避免前端显示空）
                vo.setRequesterName("已注销用户");
                vo.setRequesterAvatar("/default-avatar.png");
            }
            // 2.3 将当前VO添加到结果列表
            resultVOList.add(vo);
        }
        return resultVOList;
    }
    /**
     * 获取当前用户的好友列表（已确认的关系）
     * @return 好友列表（包含用户信息和备注）
     */
    @Override
    public List<FriendVO> getFriendList(HttpServletRequest request) {
        //当前用户的id
        String currentUserId = userInfoService.getTokenUserVO(request).getId();
        List<FriendVO> friendVOList = getBaseMapper().selectFriendsByUserId(currentUserId);
        return friendVOList;


    }
}




