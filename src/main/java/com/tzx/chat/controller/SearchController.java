package com.tzx.chat.controller;

import com.tzx.chat.annotation.AuthCheck;
import com.tzx.chat.common.BaseResponse;
import com.tzx.chat.common.ResultUtils;
import com.tzx.chat.entiy.request.search.SearchRequest;
import com.tzx.chat.entiy.vo.search.SearchResultVO;
import com.tzx.chat.entiy.vo.user.LoginUserVO;
import com.tzx.chat.service.SearchService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/search")
public class SearchController {
    @Resource
    private SearchService searchService;



    /**
     * 搜索接口，可以搜索用户，群组，信息
     * @param request
     * @param searchRequest
     * @return
     */
    @AuthCheck
    @PostMapping
    public BaseResponse<?> searchContact(HttpServletRequest request,@RequestBody SearchRequest searchRequest) {
        SearchResultVO searchResultVO = searchService.search(request,searchRequest);
        return ResultUtils.success(searchResultVO);
    }






}
