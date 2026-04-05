package dev.rbq.library_management_system.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 更新管理员权限请求 DTO
 */
public class UpdateAdminRequest {

    @NotBlank(message = "\u7528\u6237UUID\u4e0d\u80fd\u4e3a\u7a7a")
    private String userUuid;

    @NotNull(message = "\u7ba1\u7406\u5458\u72b6\u6001\u4e0d\u80fd\u4e3aNULL")
    private Boolean admin;

    // Constructors
    public UpdateAdminRequest() {
    }

    public UpdateAdminRequest(String userUuid, Boolean admin) {
        this.userUuid = userUuid;
        this.admin = admin;
    }

    // Getters and Setters
    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }
}
