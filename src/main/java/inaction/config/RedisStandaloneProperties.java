package inaction.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2020/9/22 12:53
 */
@Data
@ConfigurationProperties("lettuce.standalone")
public class RedisStandaloneProperties {
    private String host = "127.0.0.1";
    private int port = 6379;
    private int database = 0;
}
