package com.blog.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 更新用户信息请求DTO
 * 
 * @author blog
 */
@Data
@Schema(description = "更新用户信息请求")
public class UpdateUserRequest {

    @NotBlank(message = "昵称不能为空")
    @Size(max = 50, message = "昵称长度不能超过50个字符")
    @Schema(description = "昵称", example = "新昵称")
    private String nickname;

    @Schema(description = "头像URL", example = "http://example.com/avatar.jpg")
    private String avatar;
}