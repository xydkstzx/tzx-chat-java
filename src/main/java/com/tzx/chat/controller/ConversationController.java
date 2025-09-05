package com.tzx.chat.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tzx.chat.annotation.AuthCheck;
import com.tzx.chat.common.BaseResponse;
import com.tzx.chat.common.PageRequest;
import com.tzx.chat.common.ResultUtils;
import com.tzx.chat.entiy.vo.conversations.ConversationVO;
import com.tzx.chat.entiy.vo.user.LoginUserVO;
import com.tzx.chat.service.ConversationService;
import com.tzx.chat.service.UserInfoService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/conversation")
public class ConversationController {
    @Resource
    private UserInfoService userInfoService;

    @Resource
    private ConversationService conversationService;



    /**
     * 获取当前用户的会话列表（分页）
     */
    @AuthCheck
    @PostMapping("/list")
    public BaseResponse<Page<ConversationVO>> listConversations(HttpServletRequest request, @RequestBody PageRequest pageRequest) {
        LoginUserVO loginUser = userInfoService.getTokenUserVO(request);
        Page<ConversationVO> conversationPage = conversationService.listConversations(loginUser.getId(), pageRequest.getCurrent(), pageRequest.getPageSize());
        return ResultUtils.success(conversationPage);
    }

    /**
     * 获取单个会话详情
     */
    @AuthCheck
    @PostMapping("/{conversationId}")
    public BaseResponse<ConversationVO> getConversationDetail(HttpServletRequest request, @PathVariable String conversationId) {
        LoginUserVO loginUser = userInfoService.getTokenUserVO(request);
        ConversationVO conversationDTO = conversationService.getConversationDetail(loginUser.getId(), conversationId);
        return ResultUtils.success(conversationDTO);
    }


}
