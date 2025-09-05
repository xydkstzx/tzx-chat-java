package com.tzx.chat.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
@Data
@Configuration
public class AppConfig {

    @Value("${project.folder:}")
    private String projectFolder;
}
