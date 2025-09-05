package com.tzx.chat.common;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class PageResult<T>{
    private List<T> records;     // 当前页数据
    private long total;          // 总记录数
    private long pageSize;        // 每页大小
    private long current;    // 当前页码
    private long totalPages;     // 总页数

    public PageResult(List<T> records, long total, int pageSize, long current) {
        this.records = records;
        this.total = total;
        this.pageSize = pageSize;
        this.current = current;
        this.totalPages = (long) Math.ceil((double) total / pageSize);
    }
}