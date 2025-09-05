package com.tzx.chat.mapper;

import com.tzx.chat.entiy.domain.Friend;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tzx.chat.entiy.vo.fridens.FriendVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author Administrator
* @description 针对表【friend(好友关系表)】的数据库操作Mapper
* @createDate 2025-08-31 10:08:10
* @Entity generator.domain.Friend
*/
public interface FriendMapper extends BaseMapper<Friend> {

    List<FriendVO> selectFriendsByUserId(@Param("currentUserId") String currentUserId);
}




