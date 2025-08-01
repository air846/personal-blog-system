package com.blog.service.impl;

import com.blog.entity.Category;
import com.blog.mapper.CategoryMapper;
import com.blog.service.CategoryService;
import com.blog.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public Result getCategoryList() {
        List<Category> categories = categoryMapper.findAll();
        return Result.success(categories);
    }

    @Override
    public Result getCategoryById(Long id) {
        Category category = categoryMapper.findById(id);
        if (category == null) {
            return Result.error("分类不存在");
        }
        return Result.success(category);
    }

    @Override
    public Result createCategory(Category category) {
        // 检查分类名称是否已存在
        if (categoryMapper.findByName(category.getName()) != null) {
            return Result.error("分类名称已存在");
        }
        
        category.setStatus(1);
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        
        categoryMapper.insert(category);
        return Result.success("创建成功");
    }

    @Override
    public Result updateCategory(Long id, Category category) {
        Category existingCategory = categoryMapper.findById(id);
        if (existingCategory == null) {
            return Result.error("分类不存在");
        }
        
        // 检查分类名称是否已被其他分类使用
        Category nameCheck = categoryMapper.findByName(category.getName());
        if (nameCheck != null && !nameCheck.getId().equals(id)) {
            return Result.error("分类名称已存在");
        }
        
        category.setId(id);
        category.setUpdateTime(LocalDateTime.now());
        
        categoryMapper.update(category);
        return Result.success("更新成功");
    }

    @Override
    public Result deleteCategory(Long id) {
        Category category = categoryMapper.findById(id);
        if (category == null) {
            return Result.error("分类不存在");
        }
        
        // 检查是否有文章使用该分类
        int articleCount = categoryMapper.countArticlesByCategory(id);
        if (articleCount > 0) {
            return Result.error("该分类下还有文章，无法删除");
        }
        
        categoryMapper.deleteById(id);
        return Result.success("删除成功");
    }
}