package com.tzx.chat.service;

import com.tzx.chat.entiy.request.search.SearchRequest;
import com.tzx.chat.entiy.vo.search.SearchResultVO;

import javax.servlet.http.HttpServletRequest;

public interface SearchService {

    SearchResultVO search(HttpServletRequest request, SearchRequest searchRequest);
}
