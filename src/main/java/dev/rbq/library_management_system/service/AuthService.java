package dev.rbq.library_management_system.service;

import dev.rbq.library_management_system.dto.auth.AuthResponse;
import dev.rbq.library_management_system.dto.auth.LoginRequest;
import dev.rbq.library_management_system.dto.auth.RegisterRequest;
import dev.rbq.library_management_system.entity.User;
import dev.rbq.library_management_system.repository.UserRepository;
import dev.rbq.library_management_system.security.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 认证服务
 */
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * 用户注册
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        // 创建新用户
        User user = new User();
        user.setUuid(UUID.randomUUID().toString());
        user.setUsername(request.getUsername());
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setAdmin(false); // 默认为普通用户

        // 保存用户
        user = userRepository.save(user);

        return new AuthResponse(
                user.getUuid(),
                user.getUsername(),
                user.getName(),
                user.getAdmin()
        );
    }

    /**
     * 用户登录
     */
    @Transactional
    public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        try {
            // 使用 Spring Security 进行认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            // 认证成功，设置安全上下文
            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);

            // 创建或获取 session
            HttpSession session = httpRequest.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

            // 设置 session 超时时间和 Cookie maxAge（remember me 功能）
            if (Boolean.TRUE.equals(request.getRememberMe())) {
                // 记住我：1年（永久保存）
                int oneYearInSeconds = 365 * 24 * 60 * 60;
                session.setMaxInactiveInterval(oneYearInSeconds);

                // 手动设置 Cookie 的 maxAge 为 1 年
                // 这样即使浏览器关闭，Cookie 也会保留
                jakarta.servlet.http.Cookie[] cookies = httpRequest.getCookies();
                if (cookies != null) {
                    for (jakarta.servlet.http.Cookie cookie : cookies) {
                        if ("SESSIONID".equals(cookie.getName())) {
                            cookie.setMaxAge(oneYearInSeconds);
                            cookie.setPath("/");
                            cookie.setHttpOnly(true);
                            cookie.setSecure(true);
                            httpResponse.addCookie(cookie);
                            break;
                        }
                    }
                }
            } else {
                // 不记住：使用会话级别 Cookie（浏览器关闭后失效）
                // Session 在服务器上保留 30 分钟，但 Cookie 在浏览器关闭后删除
                session.setMaxInactiveInterval(30 * 60); // 30分钟

                // 确保 Cookie 的 maxAge 为 -1（会话级别）
                jakarta.servlet.http.Cookie sessionCookie = new jakarta.servlet.http.Cookie("SESSIONID", session.getId());
                sessionCookie.setMaxAge(-1); // -1 表示会话级别，浏览器关闭后删除
                sessionCookie.setPath("/");
                sessionCookie.setHttpOnly(true);
                sessionCookie.setSecure(true);
                httpResponse.addCookie(sessionCookie);
            }

            // 获取用户信息
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            User user = userDetails.getUser();

            return new AuthResponse(
                    user.getUuid(),
                    user.getUsername(),
                    user.getName(),
                    user.getAdmin()
            );

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Incorrect username or password");
        }
    }

    /**
     * 用户登出
     */
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        // 清除安全上下文
        SecurityContextHolder.clearContext();

        // 使 session 失效
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}

