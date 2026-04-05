package dev.rbq.library_management_system.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * 用户实体类
 */
@Entity
@Table(name = "user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "uuid", length = 36, nullable = false)
    private String uuid;

    @NotBlank(message = "\u7528\u6237\u540d\u4e0d\u80fd\u4e3a\u7a7a")
    @Size(max = 50, message = "\u7528\u6237\u540d\u957f\u5ea6\u4e0d\u80fd\u8d85\u540d50\u4e2a\u5b57\u7b26")  
    @Column(name = "username", length = 50, nullable = false, unique = true)
    private String username;

    @NotBlank(message = "\u6635\u79f0\u4e0d\u80fd\u4e3a\u7a7a")
    @Size(max = 12, message = "\u6635\u79f0\u957f\u5ea6\u4e0d\u80fd\u8d85\u540e12\u4e2a\u5b57\u7b26")
    @Column(name = "name", length = 12, nullable = false)
    private String name;

    @NotBlank(message = "\u5bc6\u7801\u4e0d\u80fd\u4e3a\u7a7a")
    @Size(max = 60, message = "\u5bc6\u7801\u957f\u5ea6\u4e0d\u80fd\u8d85\u548e60\u4e2a\u5b57\u7b26")
    @Column(name = "password", length = 60, nullable = false)
    private String password;

    @Column(name = "admin", nullable = false)
    private Boolean admin = false;

    // Constructors
    public User() {
    }

    public User(String uuid, String username, String name, String password, Boolean admin) {
        this.uuid = uuid;
        this.username = username;
        this.name = name;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }
}
