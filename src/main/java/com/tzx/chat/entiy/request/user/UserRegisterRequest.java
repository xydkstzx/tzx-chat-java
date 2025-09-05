package com.tzx.chat.entiy.request.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class UserRegisterRequest {
    //账号
    @NotBlank(message = "账号不能为空~")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "账号只能包含字母和数字~")
    private String userName;
    //密码
    @NotBlank(message = "密码不能为空~")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "密码必须同时包含字母和数字~")
    private String password;
    //昵称
    @NotBlank(message = "昵称不能为空~")
    @Pattern(
            regexp = "^[\\u4e00-\\u9fa5A-Za-z0-9]{1,20}$",
            message = "总长度不能超过20个字符~"
    )
    private String nickName;


}
