package dev.rbq.library_management_system.service;

import dev.rbq.library_management_system.dto.auth.AuthResponse;
import dev.rbq.library_management_system.dto.user.ChangeDisplayNameRequest;
import dev.rbq.library_management_system.dto.user.ChangePasswordRequest;
import dev.rbq.library_management_system.dto.user.ChangeUsernameRequest;
import dev.rbq.library_management_system.entity.User;
import dev.rbq.library_management_system.repository.UserRepository;
import dev.rbq.library_management_system.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户服务
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 获取当前登录用户信息
     */
    public AuthResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
            authentication.getPrincipal().equals("anonymousUser")) {
            return null;
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userDetails.getUser();

        return new AuthResponse(
                user.getUuid(),
                user.getUsername(),
                user.getName(),
                user.getAdmin()
        );
    }

    /**
     * 修改密码
     * @param request 修改密码请求
     * @throws IllegalArgumentException 如果用户不存在或旧密码错误
     */
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User currentUser = userDetails.getUser();

        // 验证用户只能修改自己的密码
        if (!currentUser.getUuid().equals(request.getUserUuid())) {
            throw new SecurityException("You are not authorized to modify other users' passwords");
        }

        // 查找用户
        User user = userRepository.findById(request.getUserUuid())
                .orElseThrow(() -> new IllegalArgumentException("User does not exist"));

        // 验证旧密码
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid old password");
        }

        // 验证新密码不能与旧密码相同
        if (request.getOldPassword().equals(request.getNewPassword())) {
            throw new IllegalArgumentException("The new password cannot be the same as the old password");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    /**
     * 修改用户名
     * @param request 修改用户名请求
     * @throws IllegalArgumentException 如果用户不存在或新用户名已被使用
     */
    @Transactional
    public AuthResponse changeUsername(ChangeUsernameRequest request) {
        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User currentUser = userDetails.getUser();

        // 验证用户只能修改自己的用户名
        if (!currentUser.getUuid().equals(request.getUserUuid())) {
            throw new SecurityException("You are not authorized to modify other users' usernames");
        }

        // 查找用户
        User user = userRepository.findById(request.getUserUuid())
                .orElseThrow(() -> new IllegalArgumentException("User does not exist"));

        // 验证新用户名不能与旧用户名相同
        if (user.getUsername().equals(request.getNewUsername())) {
            throw new IllegalArgumentException("The new username cannot be the same as the current username");
        }

        // 检查新用户名是否已被使用
        if (userRepository.existsByUsername(request.getNewUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        // 更新用户名
        user.setUsername(request.getNewUsername());
        user = userRepository.save(user);

        return new AuthResponse(
                user.getUuid(),
                user.getUsername(),
                user.getName(),
                user.getAdmin()
        );
    }

    /**
     * 修改昵称
     * @param request 修改昵称请求
     * @throws IllegalArgumentException 如果用户不存在或新昵称与旧昵称相同
     */
    @Transactional
    public AuthResponse changeDisplayName(ChangeDisplayNameRequest request) {
        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User currentUser = userDetails.getUser();

        // 验证用户只能修改自己的昵称
        if (!currentUser.getUuid().equals(request.getUserUuid())) {
            throw new SecurityException("You are not authorized to modify other users' name");
        }

        // 查找用户
        User user = userRepository.findById(request.getUserUuid())
                .orElseThrow(() -> new IllegalArgumentException("User does not exist"));

        // 验证新昵称不能与旧昵称相同
        if (user.getName().equals(request.getNewName())) {
            throw new IllegalArgumentException("The new name cannot be the same as the current name");
        }

        // 更新昵称
        user.setName(request.getNewName());
        user = userRepository.save(user);

        return new AuthResponse(
                user.getUuid(),
                user.getUsername(),
                user.getName(),
                user.getAdmin()
        );
    }
}

