package com.blog.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 分页查询请求
 * 
 * @author blog
 */
@Data
@Schema(description = "分页查询请求")
public class PageRequest {

    @Schema(description = "页码", example = "1")
    private Integer page = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer size = 10;

    @Schema(description = "排序字段")
    private String sortBy;

    @Schema(description = "排序方向：asc-升序，desc-降序", example = "desc")
    private String sortOrder = "desc";

    public Integer getPage() {
        return page != null && page > 0 ? page : 1;
    }

    public Integer getSize() {
        return size != null && size > 0 ? size : 10;
    }

    public String getSortOrder() {
        return "asc".equalsIgnoreCase(sortOrder) ? "asc" : "desc";
    }
}