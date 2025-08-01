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

    @GetMapping("/{id}")
    @Operation(summary = "获取标签详情", description = "根据标签ID获取标签详情")
    public Result<Tag> getTagDetail(@PathVariable Long id) {
        try {
            Tag tag = tagService.getById(id);
            if (tag == null) {
                return Result.notFound("标签不存在");
            }
            return Result.success(tag);
        } catch (Exception e) {
            log.error("获取标签详情失败", e);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "创建标签", description = "创建新标签，需要管理员权限")
    public Result<Void> createTag(@RequestBody Tag tag) {
        try {
            tagService.save(tag);
            return Result.success();
        } catch (Exception e) {
            log.error("创建标签失败", e);
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新标签", description = "更新标签信息，需要管理员权限")
    public Result<Void> updateTag(@PathVariable Long id, @RequestBody Tag tag) {
        try {
            tag.setId(id);
            tagService.updateById(tag);
            return Result.success();
        } catch (Exception e) {
            log.error("更新标签失败", e);
            return Result.error(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除标签", description = "删除标签，需要管理员权限")
    public Result<Void> deleteTag(@PathVariable Long id) {
        try {
            tagService.removeById(id);
            return Result.success();
        } catch (Exception e) {
            log.error("删除标签失败", e);
            return Result.error(e.getMessage());
        }
    }
}