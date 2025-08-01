package com.blog.controller;

import com.blog.common.Result;
import com.blog.entity.Tag;
import com.blog.service.TagService;
import io.swagger.v3.oas.annotations.Operation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签Controller
 * 
 * @author blog
 */
@Slf4j
@RestController
@RequestMapping("/tag")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "标签管理", description = "标签CRUD相关接口")
public class TagController {

    private final TagService tagService;

    @GetMapping("/list")
    @Operation(summary = "获取标签列表", description = "获取所有标签")
    public Result<List<Tag>> getTagList() {
        try {
            List<Tag> tags = tagService.list();
            return Result.success(tags);
        } catch (Exception e) {
            log.error("获取标签列表失败", e);
            return Result.error(e.getMessage());
        }
    }
}