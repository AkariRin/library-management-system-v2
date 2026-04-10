package dev.rbq.library_management_system.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * Redis Session 配置
 */
@Configuration
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = 365 * 24 * 60 * 60) // 最大1年，实际由代码控制
public class RedisSessionConfig {
    @Value("${spring.session.cookie.secure:true}")
    private boolean useSecureCookie;


    /**
     * 配置 Session Cookie
     * 注意：maxAge 不在这里设置，而是在登录时根据 rememberMe 动态设置
     */
    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("SESSIONID");
        serializer.setCookiePath("/");
        serializer.setDomainNamePattern("^.+?\\.(\\w+\\.[a-z]+)$");
        serializer.setUseHttpOnlyCookie(true);
        serializer.setUseSecureCookie(useSecureCookie); // 从配置文件读取
        serializer.setSameSite("Lax");
        // 不设置 CookieMaxAge，让它默认为 -1（会话级别）
        // 在登录时会根据 rememberMe 动态设置
        return serializer;
    }

    /**
     * 配置 RedisTemplate 的序列化策略
     * 这确保 Spring Session 能正确序列化和反序列化会话数据
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // Jackson 序列化器
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer =
            new Jackson2JsonRedisSerializer<>(Object.class);

        ObjectMapper mapper = new ObjectMapper();
        mapper.activateDefaultTyping(
            LaissezFaireSubTypeValidator.instance,
            ObjectMapper.DefaultTyping.NON_FINAL
        );

        // 字符串序列化器
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        // Key 使用字符串序列化
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);

        // Value 使用 Jackson 序列化
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);

        template.afterPropertiesSet();
        return template;
    }
}
