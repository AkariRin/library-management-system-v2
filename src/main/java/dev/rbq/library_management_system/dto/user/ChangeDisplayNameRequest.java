package dev.rbq.library_management_system.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 修改昵称请求 DTO
 */
public class ChangeDisplayNameRequest {

    @NotBlank(message = "The user UUID cannot be empty")
    private String userUuid;

    @NotBlank(message = "The new name cannot be left blank")
    @Size(min = 1, max = 12, message = "The new nickname must be between 1 and 12 characters in length")
    private String newName;

    // Constructors
    public ChangeDisplayNameRequest() {
    }

    public ChangeDisplayNameRequest(String userUuid, String newName) {
        this.userUuid = userUuid;
        this.newName = newName;
    }

    // Getters and Setters
    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }
}
