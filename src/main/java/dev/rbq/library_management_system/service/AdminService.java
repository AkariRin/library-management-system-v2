package dev.rbq.library_management_system.service;

import dev.rbq.library_management_system.dto.admin.AdminUserResponse;
import dev.rbq.library_management_system.dto.admin.UpdateAdminRequest;
import dev.rbq.library_management_system.entity.User;
import dev.rbq.library_management_system.repository.UserRepository;
import dev.rbq.library_management_system.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理员管理服务
 */
@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 获取所有用户列表（仅管理员可访问）
     * @return 用户列表
     */
    public List<AdminUserResponse> getAllUsers() {
        // 验证当前用户是否为管理员
        User currentUser = getCurrentUser();
        if (!currentUser.getAdmin()) {
            throw new SecurityException("Only administrators can access the user list");
        }

        // 获取所有用户并转换为响应对象
        return userRepository.findAll().stream()
                .map(user -> new AdminUserResponse(
                        user.getUuid(),
                        user.getUsername(),
                        user.getName(),
                        user.getAdmin()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 更新用户的管理员权限
     * @param request 更新请求
     * @return 更新后的用户信息
     */
    @Transactional
    public AdminUserResponse updateAdminStatus(UpdateAdminRequest request) {
        // 验证当前用户是否为管理员
        User currentUser = getCurrentUser();
        if (!currentUser.getAdmin()) {
            throw new SecurityException("Only administrators can modify user permissions");
        }

        // 验证不能修改自己的管理员权限
        if (currentUser.getUuid().equals(request.getUserUuid())) {
            throw new IllegalArgumentException("You cannot modify your own administrator privileges");
        }

        // 查找目标用户
        User targetUser = userRepository.findById(request.getUserUuid())
                .orElseThrow(() -> new IllegalArgumentException("User does not exist"));

        // 更新管理员状态
        targetUser.setAdmin(request.getAdmin());
        User savedUser = userRepository.save(targetUser);

        return new AdminUserResponse(
                savedUser.getUuid(),
                savedUser.getUsername(),
                savedUser.getName(),
                savedUser.getAdmin()
        );
    }

    /**
     * 删除用户（仅管理员可操作）
     * @param userUuid 用户UUID
     */
    @Transactional
    public void deleteUser(String userUuid) {
        // 验证当前用户是否为管理员
        User currentUser = getCurrentUser();
        if (!currentUser.getAdmin()) {
            throw new SecurityException("Only administrators can delete users");
        }

        // 验证不能删除自己
        if (currentUser.getUuid().equals(userUuid)) {
            throw new IllegalArgumentException("You cannot delete your own account");
        }

        // 验证用户是否存在
        User targetUser = userRepository.findById(userUuid)
                .orElseThrow(() -> new IllegalArgumentException("User does not exist"));

        // 删除用户
        userRepository.delete(targetUser);
    }

    /**
     * 获取当前登录用户
     * @return 当前用户
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
            authentication.getPrincipal().equals("anonymousUser")) {
            throw new SecurityException("User not logged in");
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getUser();
    }
}
