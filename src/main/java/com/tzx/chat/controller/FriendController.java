package com.tzx.chat.controller;

import com.tzx.chat.annotation.AuthCheck;
import com.tzx.chat.common.BaseResponse;
import com.tzx.chat.common.ResultUtils;
import com.tzx.chat.entiy.request.fridens.AddFriendRequest;
import com.tzx.chat.entiy.request.fridens.HandleFriendRequest;
import com.tzx.chat.entiy.vo.fridens.FriendRequestVO;
import com.tzx.chat.entiy.vo.fridens.FriendVO;
import com.tzx.chat.service.FriendService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/friend")
public class FriendController {
    @Resource
    private  FriendService friendService;

    /**
     * 添加好友
     * @param request
     * @param addFriendRequest
     * @return
     */
    @AuthCheck
    @PostMapping("/addFriends")
    public BaseResponse<Boolean> addFriends(HttpServletRequest request,@RequestBody AddFriendRequest addFriendRequest){
        boolean result = friendService.addFriends(request,addFriendRequest);
        return ResultUtils.success(result);
    }
    /**
     * 获取待处理好友请求列表
     */
    @AuthCheck
    @PostMapping("/pending")
    public BaseResponse<List<FriendRequestVO>> getPendingRequests(HttpServletRequest request) {
        List<FriendRequestVO> requestList = friendService.getPendingRequests(request);
        return ResultUtils.success(requestList);
    }
    /**
     * 处理好友请求（同意/拒绝）
     */
    @AuthCheck
    @PostMapping("/handle")
    public BaseResponse<Boolean> handleFriendRequest(@RequestBody HandleFriendRequest handleFriendRequest, HttpServletRequest request) {
        Boolean result = friendService.handleFriendRequest(request, handleFriendRequest);
        return ResultUtils.success(result);
    }
    /**
     * 获取好友列表
     */
    @AuthCheck
    @PostMapping("/list")
    public BaseResponse<List<FriendVO>> getFriendList(HttpServletRequest request) {
        List<FriendVO> friendList = friendService.getFriendList(request);
        return ResultUtils.success(friendList);
    }



}
