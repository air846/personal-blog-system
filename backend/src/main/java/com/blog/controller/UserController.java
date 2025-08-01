package com.blog.controller;

import com.blog.common.Result;
import com.blog.dto.*;
import com.blog.entity.User;
import com.blog.service.UserService;
import com.blog.utils.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

/**
 * 用户Controller
 * 
 * @author blog
 */
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "用户管理", description = "用户注册、登录、信息管理相关接口")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户注册接口")
    public Result<Void> register(@Valid @RequestBody RegisterRequest request) {
        try {
            userService.register(request.getUsername(), request.getPassword(), 
                               request.getEmail(), request.getNickname());
            return Result.success();
        } catch (Exception e) {
            log.error("用户注册失败", e);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录接口，返回JWT令牌")
    public Result<String> login(@Valid @RequestBody LoginRequest request) {
        try {
            String token = userService.login(request.getUsername(), request.getPassword());
            return Result.success("登录成功", token);
        } catch (Exception e) {
            log.error("用户登录失败", e);
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/info")
    @Operation(summary = "获取用户信息", description = "根据JWT令牌获取当前用户信息")
    public Result<UserInfoDto> getUserInfo(HttpServletRequest request) {
        try {
            String token = getTokenFromRequest(request);
            if (token == null) {
                return Result.unauthorized("未登录");
            }

            String username = jwtUtil.getUsernameFromToken(token);
            if (username == null) {
                return Result.unauthorized("令牌无效");
            }

            User user = userService.findByUsername(username);
            if (user == null) {
                return Result.notFound("用户不存在");
            }

            UserInfoDto userInfo = new UserInfoDto();
            BeanUtils.copyProperties(user, userInfo);
            // 不返回密码
            return Result.success(userInfo);
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/info")
    @Operation(summary = "更新用户信息", description = "更新当前用户的昵称和头像")
    public Result<Void> updateUserInfo(@Valid @RequestBody UpdateUserRequest request, 
                                     HttpServletRequest httpRequest) {
        try {
            String token = getTokenFromRequest(httpRequest);
            if (token == null) {
                return Result.unauthorized("未登录");
            }

            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                return Result.unauthorized("令牌无效");
            }

            userService.updateUserInfo(userId, request.getNickname(), request.getAvatar());
            return Result.success();
        } catch (Exception e) {
            log.error("更新用户信息失败", e);
            return Result.error(e.getMessage());
        }
    }

    /**
     * 从请求中获取JWT令牌
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}