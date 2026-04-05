package dev.rbq.library_management_system.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 修改用户名请求 DTO
 */
public class ChangeUsernameRequest {

    @NotBlank(message = "The user UUID cannot be empty")
    private String userUuid;

    @NotBlank(message = "The new username cannot be left blank")
    @Size(min = 3, max = 50, message = "The new username must be between 3 and 50 characters in length")
    private String newUsername;

    // Constructors
    public ChangeUsernameRequest() {
    }

    public ChangeUsernameRequest(String userUuid, String newUsername) {
        this.userUuid = userUuid;
        this.newUsername = newUsername;
    }

    // Getters and Setters
    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public String getNewUsername() {
        return newUsername;
    }

    public void setNewUsername(String newUsername) {
        this.newUsername = newUsername;
    }
}
