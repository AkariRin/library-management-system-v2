package dev.rbq.library_management_system.controller;

import dev.rbq.library_management_system.dto.ApiResponse;
import dev.rbq.library_management_system.dto.auth.AuthResponse;
import dev.rbq.library_management_system.dto.user.ChangeDisplayNameRequest;
import dev.rbq.library_management_system.dto.user.ChangePasswordRequest;
import dev.rbq.library_management_system.dto.user.ChangeUsernameRequest;
import dev.rbq.library_management_system.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthResponse>> getCurrentUser() {
        try {
            AuthResponse response = userService.getCurrentUser();
            if (response == null) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("\u672a\u6388\u6743"));
            }
            return ResponseEntity.ok(ApiResponse.success("\u7528\u6237\u4fe1\u606f\u83b7\u53d6\u6210\u529f", response));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("\u83b7\u53d6\u7528\u6237\u4fe1\u606f\u5931\u8d25:" + e.getMessage()));
        }
    }

    /**
     * 修改密码
     * @param request 修改密码请求
     */
    @PostMapping("/change-pass")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request) {
        try {
            userService.changePassword(request);
            return ResponseEntity.ok(ApiResponse.success("\u5bc6\u7801\u4fee\u6539\u6210\u529f", null));
        } catch (SecurityException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (BadCredentialsException | IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("\u5bc6\u7801\u4fee\u6539\u5931\u8d25:" + e.getMessage()));
        }
    }

    /**
     * 修改用户名
     * @param request 修改用户名请求
     */
    @PostMapping("/change-username")
    public ResponseEntity<ApiResponse<AuthResponse>> changeUsername(
            @Valid @RequestBody ChangeUsernameRequest request) {
        try {
            AuthResponse response = userService.changeUsername(request);
            return ResponseEntity.ok(ApiResponse.success("\u7528\u6237\u540d\u4fee\u6539\u6210\u529f", response));
        } catch (SecurityException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("\u7528\u6237\u540d\u4fee\u6539\u5931\u8d25:" + e.getMessage()));
        }
    }

    /**
     * 修改昵称
     * @param request 修改昵称请求
     */
    @PostMapping("/change-name")
    public ResponseEntity<ApiResponse<AuthResponse>> changeDisplayName(
            @Valid @RequestBody ChangeDisplayNameRequest request) {
        try {
            AuthResponse response = userService.changeDisplayName(request);
            return ResponseEntity.ok(ApiResponse.success("\u6635\u79f0\u4fee\u6539\u6210\u529f", response));
        } catch (SecurityException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("\u6635\u79f0\u4fee\u6539\u5931\u8d25:" + e.getMessage()));
        }
    }
}
