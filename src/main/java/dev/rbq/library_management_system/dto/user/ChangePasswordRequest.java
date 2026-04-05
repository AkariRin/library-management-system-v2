package dev.rbq.library_management_system.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 修改密码请求 DTO
 */
public class ChangePasswordRequest {

    @NotBlank(message = "The user UUID cannot be empty")
    private String userUuid;

    @NotBlank(message = "The old password cannot be left blank")
    private String oldPassword;

    @NotBlank(message = "The new password cannot be left blank")
    @Size(min = 6, max = 50, message = "The new password must be between 6 and 50 characters in length")
    private String newPassword;

    // Constructors
    public ChangePasswordRequest() {
    }

    public ChangePasswordRequest(String userUuid, String oldPassword, String newPassword) {
        this.userUuid = userUuid;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    // Getters and Setters
    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
