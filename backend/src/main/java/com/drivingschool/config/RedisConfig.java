package com.drivingschool.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 基础配置
 * <p>
 * 配置 RedisTemplate 的序列化方式：
 * - Key 使用字符串序列化，便于在 Redis 客户端中查看；
 * - Value 使用 JSON 序列化，支持复杂对象的存取。
 * </p>
 */
@Configuration
public class RedisConfig {

    /**
     * 创建 RedisTemplate 并配置序列化方式
     *
     * @param factory Redis 连接工厂，由 Spring Boot 自动注入
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // Key 使用字符串序列化，方便在 Redis 中查看
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // Value 使用 JSON 序列化，支持对象存储
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }
}
