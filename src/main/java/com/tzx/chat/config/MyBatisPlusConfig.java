package com.tzx.chat.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus配置类，配置分页插件
 */
@Configuration
public class MyBatisPlusConfig {

    /**
     * 注册MyBatis-Plus拦截器，添加分页功能
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加分页插件，指定数据库类型（根据实际项目的数据库类型选择）
        // 支持的数据库类型：MYSQL、ORACLE、DB2、H2、SQL_SERVER等
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        // 配置分页参数溢出处理：当页码超过最大页时，自动查询最后一页
        paginationInnerInterceptor.setOverflow(true);
        // 配置默认每页最大记录数，-1表示无限制
        paginationInnerInterceptor.setMaxLimit(500L);
        interceptor.addInnerInterceptor(paginationInnerInterceptor);
        return interceptor;
    }
}