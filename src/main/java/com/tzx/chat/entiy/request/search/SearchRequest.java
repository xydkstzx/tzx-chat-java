package com.tzx.chat.entiy.request.search;

import com.tzx.chat.common.PageRequest;
import lombok.Data;

@Data
public class SearchRequest extends PageRequest {

    private String searchValue;

    private String type;

    private String userId;

}
