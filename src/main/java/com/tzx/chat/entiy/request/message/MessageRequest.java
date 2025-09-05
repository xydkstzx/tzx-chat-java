package com.tzx.chat.entiy.request.message;

import com.tzx.chat.common.PageRequest;
import lombok.Data;

@Data
public class MessageRequest extends PageRequest {
    private String targetUserId;

}
