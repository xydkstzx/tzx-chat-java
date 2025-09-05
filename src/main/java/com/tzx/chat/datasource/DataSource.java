package com.tzx.chat.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tzx.chat.entiy.domain.UserInfo;
import com.tzx.chat.entiy.request.search.SearchRequest;
import com.tzx.chat.entiy.vo.search.SearchResultVO;

public interface DataSource<T> {

    Page<T> doSearch(SearchRequest searchRequest);
}
