package dev.rbq.library_management_system.dto.admin;

/**
 * 管理员用户信息响应 DTO
 */
public class AdminUserResponse {

    private String uuid;
    private String username;
    private String name;
    private Boolean admin;

    // Constructors
    public AdminUserResponse() {
    }

    public AdminUserResponse(String uuid, String username, String name, Boolean admin) {
        this.uuid = uuid;
        this.username = username;
        this.name = name;
        this.admin = admin;
    }

    // Getters and Setters
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

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

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }
}
