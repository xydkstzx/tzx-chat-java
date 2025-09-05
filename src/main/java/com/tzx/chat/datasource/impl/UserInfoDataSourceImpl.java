package com.tzx.chat.datasource.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tzx.chat.datasource.DataSource;
import com.tzx.chat.entiy.domain.UserInfo;
import com.tzx.chat.entiy.request.search.SearchRequest;
import com.tzx.chat.entiy.vo.fridens.FriendVO;
import com.tzx.chat.entiy.vo.search.SearchResultVO;
import com.tzx.chat.mapper.FriendMapper;
import com.tzx.chat.mapper.UserInfoMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserInfoDataSourceImpl implements DataSource<UserInfo> {
    @Resource
    private FriendMapper friendMapper;
    @Resource
    private UserInfoMapper userInfoMapper;
    /**
     * 搜索用户数据
     * @param searchRequest
     * @return
     */
    @Override
    public Page<UserInfo> doSearch(SearchRequest searchRequest) {
        //创建分页对象（Page 构造器：pageNum-页码，pageSize-每页条数）
        Page<UserInfo> page = new Page<>(searchRequest.getCurrent(), searchRequest.getPageSize());
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<UserInfo>();
        //检索用户账号
        queryWrapper.like(UserInfo::getUserName,searchRequest.getSearchValue());
        Page<UserInfo> userInfoPage = userInfoMapper.selectPage(page, queryWrapper);
        //查询当前登录用户的好友数据
        String currentUserId = searchRequest.getUserId();

        //对比用户id，如果相当就是好友
        if (currentUserId != null && userInfoPage.getRecords() != null && !userInfoPage.getRecords().isEmpty()) {
            // 4.1 查询当前登录用户的所有有效好友
            List<FriendVO> friendVOList = friendMapper.selectFriendsByUserId(currentUserId);
            // 4.2 提取好友ID到Set集合，便于快速判断（假设FriendVO中好友ID字段为friendId）
            Set<String> friendIdSet = friendVOList.stream()
                    .map(FriendVO::getFriendId)
                    .collect(Collectors.toSet());
            // 4.3 为每个搜索到的用户设置是否为好友的标识
            userInfoPage.getRecords().forEach(userInfo -> {
                // 判断当前用户ID是否在好友ID集合中
                boolean isFriend = friendIdSet.contains(userInfo.getId());
                userInfo.setIsFriend(isFriend);
            });
        }



        //构建分页返回参数
        return userInfoPage;
    }
}
