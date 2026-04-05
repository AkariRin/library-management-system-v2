package dev.rbq.library_management_system.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 用户注册请求 DTO
 */
public class RegisterRequest {

    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3, max = 50, message = "Username length must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Name cannot be empty")
    @Size(min = 1, max = 12, message = "Names must be between 1 and 12 characters in length")
    private String name;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, max = 50, message = "Password length must be between 6 and 50 characters")
    private String password;

    // Constructors
    public RegisterRequest() {
    }

    public RegisterRequest(String username, String name, String password) {
        this.username = username;
        this.name = name;
        this.password = password;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
