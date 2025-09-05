package com.tzx.chat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@MapperScan("com.tzx.chat.mapper")
@EnableAsync
@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
public class TzxChatJavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(TzxChatJavaApplication.class, args);
    }

}
