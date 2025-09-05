package com.tzx.chat.service;

import com.tzx.chat.entiy.domain.Friend;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tzx.chat.entiy.request.fridens.AddFriendRequest;
import com.tzx.chat.entiy.request.fridens.HandleFriendRequest;
import com.tzx.chat.entiy.vo.fridens.FriendRequestVO;
import com.tzx.chat.entiy.vo.fridens.FriendVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author Administrator
* @description 针对表【friend(好友关系表)】的数据库操作Service
* @createDate 2025-08-31 10:08:10
*/
public interface FriendService extends IService<Friend> {
    /**
     * 添加好友
     * @param request
     * @param addFriendRequest
     * @return
     */
    boolean addFriends(HttpServletRequest request, AddFriendRequest addFriendRequest);

    /**
     * 处理好友请求
     * @param request
     * @param request1
     * @return
     */
    Boolean handleFriendRequest(HttpServletRequest request, HandleFriendRequest request1);

    List<FriendRequestVO> getPendingRequests(HttpServletRequest request);

    List<FriendVO> getFriendList(HttpServletRequest request);
}
