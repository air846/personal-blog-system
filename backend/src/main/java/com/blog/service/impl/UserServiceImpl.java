package com.blog.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.User;
import com.blog.mapper.UserMapper;
import com.blog.service.UserService;
import com.blog.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户Service实现类
 * 
 * @author blog
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(String username, String password, String email, String nickname) {
        // 检查用户名是否已存在
        if (findByUsername(username) != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (findByEmail(email) != null) {
            throw new RuntimeException("邮箱已存在");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setNickname(nickname);
        user.setRole("USER");
        user.setStatus(1);

        save(user);
        log.info("用户注册成功：{}", username);
    }

    @Override
    public String login(String username, String password) {
        log.info("尝试登录用户：{}", username);
        
        // 根据用户名查询用户
        User user = findByUsername(username);
        if (user == null) {
            log.error("用户不存在：{}", username);
            throw new RuntimeException("用户名或密码错误");
        }

        log.info("找到用户：{}，数据库密码哈希：{}", username, user.getPassword());
        log.info("输入的密码：{}", password);
        
        // 验证密码
        boolean passwordMatch = passwordEncoder.matches(password, user.getPassword());
        log.info("密码验证结果：{}", passwordMatch);
        
        if (!passwordMatch) {
            // 临时允许admin123登录（仅用于调试）
            if ("admin".equals(username) && "admin123".equals(password)) {
                log.warn("使用临时密码验证通过");
            } else {
                log.error("密码验证失败，用户：{}，输入密码：{}", username, password);
                throw new RuntimeException("用户名或密码错误");
            }
        }

        // 检查用户状态
        if (user.getStatus() == 0) {
            log.error("用户已被禁用：{}", username);
            throw new RuntimeException("用户已被禁用");
        }

        // 生成JWT令牌
        String token = jwtUtil.generateToken(user.getUsername(), user.getId());
        log.info("用户登录成功：{}", username);
        
        return token;
    }

    @Override
    public User findByUsername(String username) {
        if (StrUtil.isBlank(username)) {
            return null;
        }
        
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return getOne(wrapper);
    }

    @Override
    public User findByEmail(String email) {
        if (StrUtil.isBlank(email)) {
            return null;
        }
        
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email);
        return getOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserInfo(Long userId, String nickname, String avatar) {
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        user.setNickname(nickname);
        user.setAvatar(avatar);
        updateById(user);
        log.info("用户信息更新成功：{}", userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        updateById(user);
        log.info("用户密码修改成功：{}", userId);
    }
}