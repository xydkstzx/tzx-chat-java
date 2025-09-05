package com.tzx.chat.datasource.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tzx.chat.datasource.DataSource;
import com.tzx.chat.entiy.domain.Message;
import com.tzx.chat.entiy.domain.UserInfo;
import com.tzx.chat.entiy.enums.MessageTypeEnum;
import com.tzx.chat.entiy.request.search.SearchRequest;
import com.tzx.chat.service.MessageService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class MessageDataSourceImpl implements DataSource<Message> {
    @Resource
    private MessageService messageService;



    /**
     * 返回聊天信息
     * @param searchRequest
     * @return
     */
    @Override
    public Page<Message> doSearch(SearchRequest searchRequest) {
        Page<Message> page = new Page<>(searchRequest.getCurrent(), searchRequest.getPageSize());
        String searchValue = searchRequest.getSearchValue();
        //todo 查询我和所有好友的信息
        LambdaQueryWrapper<Message> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Message::getContent, searchValue).eq(Message::getStatus, MessageTypeEnum.TEXT.getCode());
        Page<Message> messagePage = messageService.page(page, queryWrapper);
        return messagePage;
    }
}
