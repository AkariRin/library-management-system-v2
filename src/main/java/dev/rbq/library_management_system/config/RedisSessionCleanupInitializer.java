package dev.rbq.library_management_system.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Redis Session 清理初始化器
 * 在应用启动时清除旧的会话数据，确保序列化兼容性
 */
@Component
public class RedisSessionCleanupInitializer {

    private static final Logger logger = LoggerFactory.getLogger(RedisSessionCleanupInitializer.class);

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 在应用启动后清除旧的会话数据
     */
    @EventListener(ApplicationReadyEvent.class)
    public void cleanupOldSessions() {
        try {
            logger.info("正在清除旧的 Redis 会话数据...");

            // 获取所有匹配 library:session* 的 key
            Set<String> sessionKeys = redisTemplate.keys("library:session*");

            if (sessionKeys != null && !sessionKeys.isEmpty()) {
                logger.info("找到 {} 个旧会话 key，准备删除...", sessionKeys.size());
                redisTemplate.delete(sessionKeys);
                logger.info("已清除 {} 个旧会话数据", sessionKeys.size());
            } else {
                logger.info("未找到旧的会话数据");
            }
        } catch (Exception e) {
            logger.warn("清除旧会话数据时出现错误（非致命）: {}", e.getMessage());
            // 不中断应用启动
        }
    }
}

