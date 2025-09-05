package com.tzx.chat.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tzx.chat.datasource.DataSource;
import com.tzx.chat.datasource.impl.MessageDataSourceImpl;
import com.tzx.chat.datasource.impl.UserInfoDataSourceImpl;
import com.tzx.chat.entiy.domain.Message;
import com.tzx.chat.entiy.domain.UserInfo;
import com.tzx.chat.entiy.enums.ErrorCode;
import com.tzx.chat.entiy.enums.SearchType;
import com.tzx.chat.entiy.request.search.SearchRequest;
import com.tzx.chat.entiy.vo.search.SearchResultVO;
import com.tzx.chat.entiy.vo.user.LoginUserVO;
import com.tzx.chat.exception.BusinessException;
import com.tzx.chat.service.SearchService;
import com.tzx.chat.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class SearchServiceImpl implements SearchService {

    @Resource
    private UserInfoService userInfoService;
    @Resource
    private UserInfoDataSourceImpl userInfoDataSource;

    @Resource
    private MessageDataSourceImpl messageDataSource;
    /**
     * 搜索
     * @param request
     * @param searchRequest 搜索内容
     * @return
     */
    @Override
    public SearchResultVO search(HttpServletRequest request, SearchRequest searchRequest) {
        String type = searchRequest.getType();
        SearchType searchCode = SearchType.getByCode(type);
        LoginUserVO tokenUserVO = userInfoService.getTokenUserVO(request);
        //设置用户id
        searchRequest.setUserId(tokenUserVO.getId());
        //搜索全部数据

        if (searchCode == null) {
            log.info("查询全部数据");
            SearchResultVO searchResultVO = new SearchResultVO();
            //搜索用户
            CompletableFuture<Page<UserInfo>> userTask = CompletableFuture.supplyAsync(() ->{
                Page<UserInfo> userInfoPage = userInfoDataSource.doSearch(searchRequest);
                return userInfoPage;
            });

//            CompletableFuture<Page<Message>> messageTask = CompletableFuture.supplyAsync(() ->{
//                Page<Message> messagePage = messageDataSource.doSearch(searchRequest);
//                return messagePage;
//            });

            CompletableFuture.allOf(userTask).join();
            try {
                Page<UserInfo> userInfoPage = userTask.get();
                //Page<Message> messagePage = messageTask.get();
                searchResultVO.setUserInfoPage(userInfoPage);
                //searchResultVO.setMessagesPage(messagePage);
                return searchResultVO;
            } catch (Exception e) {
                log.error("查询异常", e);
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "查询异常");
            }
        }else{
            //搜索好友信息
            SearchResultVO searchResultVO = new SearchResultVO();
            //搜索指定类型数据
            DataSource dataSource = null;
            switch (searchCode) {
                case FRIEND:
                    dataSource = userInfoDataSource;
                    break;
//                case CHAT:
//                    dataSource = messageDataSource;
//                    break;
            }
            //todo 搜索聊天用户信息

            //todo 搜索群组信息
            Page page = dataSource.doSearch(searchRequest);
            searchResultVO.setDataList(page);
            return searchResultVO;
        }
    }
}
