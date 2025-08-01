package com.blog.service.impl;

import com.blog.entity.User;
import com.blog.mapper.UserMapper;
import com.blog.service.UserService;
import com.blog.util.JwtUtil;
import com.blog.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Result register(User user) {
        // 检查用户名是否已存在
        if (userMapper.findByUsername(user.getUsername()) != null) {
            return Result.error("用户名已存在");
        }
        
        // 检查邮箱是否已存在
        if (userMapper.findByEmail(user.getEmail()) != null) {
            return Result.error("邮箱已被注册");
        }
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        userMapper.insert(user);
        return Result.success("注册成功");
    }

    @Override
    public Result login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        if (user.getStatus() != 1) {
            return Result.error("账号已被禁用");
        }
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return Result.error("密码错误");
        }
        
        // 生成JWT令牌
        String token = jwtUtil.generateToken(user.getUsername(), user.getId());
        
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);
        
        return Result.success(data);
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
        return userMapper.findByEmail(email);
    }

    @Override
    public User findById(Long id) {
        return userMapper.findById(id);
    }

    @Override
    public Result updateUser(User user) {
        user.setUpdateTime(LocalDateTime.now());
        userMapper.update(user);
        return Result.success("更新成功");
    }

    @Override
    public Result changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.findById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return Result.error("原密码错误");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateTime(LocalDateTime.now());
        userMapper.update(user);
        
        return Result.success("密码修改成功");
    }
}