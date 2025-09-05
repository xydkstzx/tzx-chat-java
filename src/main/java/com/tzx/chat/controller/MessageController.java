package com.tzx.chat.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tzx.chat.annotation.AuthCheck;
import com.tzx.chat.common.BaseResponse;
import com.tzx.chat.common.PageRequest;
import com.tzx.chat.common.ResultUtils;
import com.tzx.chat.entiy.domain.Message;
import com.tzx.chat.entiy.request.message.MessageRequest;
import com.tzx.chat.entiy.request.message.SendMessageRequest;
import com.tzx.chat.entiy.vo.message.MessageVO;
import com.tzx.chat.service.MessageService;
import com.tzx.chat.service.UserInfoService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;

@RestController
@RequestMapping("/message")
public class MessageController {
    @Resource
    private UserInfoService userInfoService;
    @Resource
    private MessageService messageService;



    @AuthCheck
    @PostMapping("/records")
    public BaseResponse<Page<MessageVO>> getPrivateChatRecords(@RequestBody MessageRequest messageRequest, HttpServletRequest request) {
        // 获取当前登录用户ID（从token中解析）
        String currentUserId = userInfoService.getTokenUserVO(request).getId();
        Page<MessageVO> records = messageService.getPrivateChatRecords(currentUserId, messageRequest.getTargetUserId(), messageRequest.getCurrent(), messageRequest.getPageSize());
        return ResultUtils.success(records);
    }
    /**
     * 发送私聊信息
     * @param request
     * @param sendMessageRequest
     * @return
     */
    @AuthCheck
    @PostMapping("/send")
    public BaseResponse<MessageVO> send(HttpServletRequest request,@RequestBody SendMessageRequest sendMessageRequest) {
        String currentUserId = userInfoService.getTokenUserVO(request).getId();
        MessageVO result = messageService.send(currentUserId,sendMessageRequest);
        return ResultUtils.success(result);
    }










}
