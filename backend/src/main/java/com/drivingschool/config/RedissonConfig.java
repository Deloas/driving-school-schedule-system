package com.drivingschool.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson 分布式锁客户端配置
 * <p>
 * M5/M6 阶段预约接口将使用 Redisson 分布式锁，
 * 锁粒度为 scheduleId，确保同一排班并发预约时不会超过最大容量。
 * 本阶段仅初始化客户端，不编写加锁逻辑。
 * </p>
 */
@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.data.redis.port:26379}")
    private int redisPort;

    /**
     * 创建 Redisson 客户端 Bean
     * <p>
     * 使用单节点模式连接 Redis。如果 application-dev.yml 中配置了 Redis 密码，
     * 需要在此处追加 .setPassword() 配置。
     * </p>
     */
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        // 单节点模式连接 Redis
        config.useSingleServer()
                .setAddress("redis://" + redisHost + ":" + redisPort)
                // 本地开发环境 Redis 无密码，无需设置
                // .setPassword("your_password")
                .setConnectionMinimumIdleSize(2)
                .setConnectionPoolSize(8);

        return Redisson.create(config);
    }
}
