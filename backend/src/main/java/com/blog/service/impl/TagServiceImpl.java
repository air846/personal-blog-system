package com.blog.service.impl;

import com.blog.entity.Tag;
import com.blog.mapper.TagMapper;
import com.blog.service.TagService;
import com.blog.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TagServiceImpl implements TagService {

    @Autowired
    private TagMapper tagMapper;

    @Override
    public Result getTagList() {
        List<Tag> tags = tagMapper.findAll();
        return Result.success(tags);
    }

    @Override
    public Result getTagById(Long id) {
        Tag tag = tagMapper.findById(id);
        if (tag == null) {
            return Result.error("标签不存在");
        }
        return Result.success(tag);
    }

    @Override
    public Result createTag(Tag tag) {
        // 检查标签名称是否已存在
        if (tagMapper.findByName(tag.getName()) != null) {
            return Result.error("标签名称已存在");
        }
        
        tag.setCreateTime(LocalDateTime.now());
        tag.setUpdateTime(LocalDateTime.now());
        
        tagMapper.insert(tag);
        return Result.success("创建成功");
    }

    @Override
    public Result updateTag(Long id, Tag tag) {
        Tag existingTag = tagMapper.findById(id);
        if (existingTag == null) {
            return Result.error("标签不存在");
        }
        
        // 检查标签名称是否已被其他标签使用
        Tag nameCheck = tagMapper.findByName(tag.getName());
        if (nameCheck != null && !nameCheck.getId().equals(id)) {
            return Result.error("标签名称已存在");
        }
        
        tag.setId(id);
        tag.setUpdateTime(LocalDateTime.now());
        
        tagMapper.update(tag);
        return Result.success("更新成功");
    }

    @Override
    public Result deleteTag(Long id) {
        Tag tag = tagMapper.findById(id);
        if (tag == null) {
            return Result.error("标签不存在");
        }
        
        // 检查是否有文章使用该标签
        int articleCount = tagMapper.countArticlesByTag(id);
        if (articleCount > 0) {
            return Result.error("该标签下还有文章，无法删除");
        }
        
        tagMapper.deleteById(id);
        return Result.success("删除成功");
    }

    @Override
    public Result getHotTags(int limit) {
        List<Tag> tags = tagMapper.findHotTags(limit);
        return Result.success(tags);
    }
}