package com.tzx.chat.entiy.vo.search;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tzx.chat.entiy.domain.Message;
import com.tzx.chat.entiy.domain.UserInfo;
import lombok.Data;


/**
 *
 */
@Data
public class SearchResultVO {
    /**
     * 用户只搜索用户时展示的数据
     */
    private Page<UserInfo> userInfoPage;

    /**
     * todo 待实现
     */
    private Page<Message> messagesPage;
    /**
     * todo 群组信息
     */
    /**
     * 群组，信息，好友，数据，用户全面搜索时展示的数据
     */
    private Page<?> dataList;

}
