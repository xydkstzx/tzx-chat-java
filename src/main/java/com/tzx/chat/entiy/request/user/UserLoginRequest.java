package com.tzx.chat.entiy.request.user;

import lombok.Data;

@Data
public class UserLoginRequest {

    private String userName;

    private String password;
}
