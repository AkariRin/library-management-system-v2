package dev.rbq.library_management_system.config;

import dev.rbq.library_management_system.entity.User;
import dev.rbq.library_management_system.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.UUID;

/**
 * 默认管理员初始化器
 * 在应用启动时，如果数据库中没有管理员账户，则创建一个默认管理员
 */
@Component
public class DefaultAdminInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DefaultAdminInitializer.class);

    private static final String DEFAULT_ADMIN_NAME = "Admin";
    private static final String USERNAME_PREFIX = "admin_";
    private static final int MAX_USERNAME_ATTEMPTS = 100;

    // 密码字符集
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&*";
    private static final String ALL_CHARS = UPPERCASE + LOWERCASE + DIGITS + SPECIAL_CHARS;
    private static final int PASSWORD_LENGTH = 12;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final SecureRandom random = new SecureRandom();

    /**
     * 在应用完全启动后执行，此时JPA已经完成数据库初始化
     */
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initializeDefaultAdmin() {
        logger.info("Checking for existing admin accounts...");

        // 检查是否存在管理员账户
        boolean hasAdmin = userRepository.existsByAdmin(true);

        if (!hasAdmin) {
            logger.warn("No admin account found in database. Creating default admin account...");

            // 生成唯一的用户名
            String username = generateUniqueUsername();
            if (username == null) {
                logger.error("Failed to generate unique username after {} attempts. Aborting admin creation.", MAX_USERNAME_ATTEMPTS);
                return;
            }

            // 生成随机密码
            String password = generateRandomPassword();

            // 创建默认管理员账户
            User defaultAdmin = new User();
            defaultAdmin.setUuid(UUID.randomUUID().toString());
            defaultAdmin.setUsername(username);
            defaultAdmin.setName(DEFAULT_ADMIN_NAME);
            defaultAdmin.setPassword(passwordEncoder.encode(password));
            defaultAdmin.setAdmin(true);

            userRepository.save(defaultAdmin);

            // 在控制台打印账号密码
            logger.warn("=".repeat(80));
            logger.warn("DEFAULT ADMIN ACCOUNT CREATED");
            logger.warn("=".repeat(80));
            logger.warn("Username: {}", username);
            logger.warn("Password: {}", password);
            logger.warn("=".repeat(80));
            logger.warn("PLEASE CHANGE THE DEFAULT PASSWORD IMMEDIATELY AFTER FIRST LOGIN!");
            logger.warn("=".repeat(80));
        } else {
            logger.info("Admin account(s) already exist in database. Skipping default admin creation.");
        }
    }

    /**
     * 生成唯一的用户名
     * @return 唯一的用户名，如果失败则返回null
     */
    private String generateUniqueUsername() {
        for (int i = 0; i < MAX_USERNAME_ATTEMPTS; i++) {
            String username = USERNAME_PREFIX + generateRandomString(8, LOWERCASE + DIGITS);

            // 检查用户名是否已存在
            if (!userRepository.existsByUsername(username)) {
                return username;
            }
        }
        return null;
    }

    /**
     * 生成随机密码
     * 密码包含大写字母、小写字母、数字和特殊字符
     * @return 随机密码
     */
    private String generateRandomPassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        // 确保密码至少包含每种类型的字符
        password.append(UPPERCASE.charAt(random.nextInt(UPPERCASE.length())));
        password.append(LOWERCASE.charAt(random.nextInt(LOWERCASE.length())));
        password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        password.append(SPECIAL_CHARS.charAt(random.nextInt(SPECIAL_CHARS.length())));

        // 填充剩余字符
        for (int i = 4; i < PASSWORD_LENGTH; i++) {
            password.append(ALL_CHARS.charAt(random.nextInt(ALL_CHARS.length())));
        }

        // 打乱字符顺序
        return shuffleString(password.toString());
    }

    /**
     * 生成随机字符串
     * @param length 长度
     * @param charset 字符集
     * @return 随机字符串
     */
    private String generateRandomString(int length, String charset) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(charset.charAt(random.nextInt(charset.length())));
        }
        return sb.toString();
    }

    /**
     * 打乱字符串
     * @param input 输入字符串
     * @return 打乱后的字符串
     */
    private String shuffleString(String input) {
        char[] chars = input.toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }
}
