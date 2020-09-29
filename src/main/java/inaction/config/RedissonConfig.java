package inaction.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2020/9/28 19:04
 */
@Configuration
@EnableConfigurationProperties(RedissonProperties.class)
public class RedissonConfig {

    private static final String SCHEMA = "redis://";

    @Bean
    public RedissonClient redissonClient(RedissonProperties properties) {
        Config config = new Config();
        config.useSingleServer().setAddress(SCHEMA + properties.getHost() + ":" + properties.getPort());
        return Redisson.create(config);
    }

}
