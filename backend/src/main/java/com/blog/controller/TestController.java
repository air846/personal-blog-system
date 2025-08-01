package com.blog.controller;

import com.blog.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试Controller
 * 
 * @author blog
 */
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
@Tag(name = "测试接口", description = "用于测试系统基本功能")
public class TestController {

    private final PasswordEncoder passwordEncoder;

    @GetMapping("/hello")
    @Operation(summary = "测试接口", description = "返回Hello World")
    public Result<String> hello() {
        return Result.success("Hello World! 博客系统启动成功！");
    }

    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "检查系统健康状态")
    public Result<String> health() {
        return Result.success("系统运行正常");
    }

    @GetMapping("/encode-password")
    @Operation(summary = "密码编码", description = "生成密码的BCrypt哈希值")
    public Result<String> encodePassword(@RequestParam String password) {
        String encoded = passwordEncoder.encode(password);
        return Result.success("编码成功", encoded);
    }
}