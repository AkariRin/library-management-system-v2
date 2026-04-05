package dev.rbq.library_management_system.repository;

import dev.rbq.library_management_system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户数据访问接口
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    /**
     * 根据用户名查找用户
     * @param username 用户名
     * @return 用户（如果存在）
     */
    Optional<User> findByUsername(String username);

    /**
     * 检查用户名是否存在
     * @param username 用户名
     * @return true 如果用户名已存在，否则 false
     */
    boolean existsByUsername(String username);

    /**
     * 检查是否存在管理员账户
     * @param admin 管理员标识
     * @return true 如果存在管理员账户，否则 false
     */
    boolean existsByAdmin(Boolean admin);
}
