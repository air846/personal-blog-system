package com.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.entity.User;

/**
 * 用户服务接口
 * 
 * @author blog
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     */
    void register(String username, String password, String email, String nickname);

    /**
     * 用户登录
     */
    String login(String username, String password);

    /**
     * 根据用户名查找用户
     */
    User findByUsername(String username);

    /**
     * 根据邮箱查找用户
     */
    User findByEmail(String email);

    /**
     * 更新用户信息
     */
    void updateUserInfo(Long userId, String nickname, String avatar);
}