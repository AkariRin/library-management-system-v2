package dev.rbq.library_management_system.controller;

import dev.rbq.library_management_system.dto.ApiResponse;
import dev.rbq.library_management_system.dto.admin.AdminUserResponse;
import dev.rbq.library_management_system.dto.admin.UpdateAdminRequest;
import dev.rbq.library_management_system.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员管理控制器
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * 获取所有用户列表
     * @return 用户列表
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<AdminUserResponse>>> getAllUsers() {
        try {
            List<AdminUserResponse> users = adminService.getAllUsers();
            return ResponseEntity.ok(ApiResponse.success("\u7528\u6237\u5217\u8868\u83b7\u53d6\u6210\u529f", users));
        } catch (SecurityException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("\u83b7\u53d6\u7528\u6237\u5217\u8868\u5931\u8d25: " + e.getMessage()));
        }
    }

    /**
     * 更新用户的管理员权限
     * @param request 更新请求
     * @return 更新后的用户信息
     */
    @PostMapping("/users/admin-status")
    public ResponseEntity<ApiResponse<AdminUserResponse>> updateAdminStatus(
            @Valid @RequestBody UpdateAdminRequest request) {
        try {
            AdminUserResponse response = adminService.updateAdminStatus(request);
            String message = request.getAdmin()
                    ? "\u7ba1\u7406\u5458\u6743\u9650\u6388\u4e88\u6210\u529f"
                    : "\u7ba1\u7406\u5458\u6743\u9650\u5df2\u5410\u9500";
            return ResponseEntity.ok(ApiResponse.success(message, response));
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
                    .body(ApiResponse.error("\u66f4\u65b0\u7ba1\u7406\u5458\u6743\u9650\u5931\u8d25: " + e.getMessage()));
        }
    }

    /**
     * 删除用户
     * @param userUuid 用户UUID
     * @return 删除结果
     */
    @DeleteMapping("/users/{userUuid}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable String userUuid) {
        try {
            adminService.deleteUser(userUuid);
            return ResponseEntity.ok(ApiResponse.success("\u7528\u6237\u5220\u9664\u6210\u529f", null));
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
                    .body(ApiResponse.error("\u7528\u6237\u5220\u9664\u5931\u8d25: " + e.getMessage()));
        }
    }
}
