package inaction.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2020/9/29 16:42
 */
@Data
@ConfigurationProperties(prefix = "zoo")
public class ZooProperties {
    private String host = "127.0.0.1";
    private int port = 2181;
}
