package inaction.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2020/9/28 19:05
 */
@Data
@ConfigurationProperties(prefix = "redisson")
public class RedissonProperties {
    private String host = "127.0.0.1";
    private int port = 6379;
}
