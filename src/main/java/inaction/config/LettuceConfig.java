package inaction.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2020/9/22 11:42
 */
@Configuration
@EnableConfigurationProperties(RedisStandaloneProperties.class)
public class LettuceConfig {

    @Bean
    public RedisClient redisClient(RedisStandaloneProperties properties) {
        RedisURI uri = RedisURI.create(properties.getHost(), properties.getPort());
        uri.setDatabase(properties.getDatabase());
        return RedisClient.create(uri);
    }
}

