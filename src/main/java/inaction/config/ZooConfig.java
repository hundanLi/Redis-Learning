package inaction.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2020/9/29 16:43
 */
@Configuration
@EnableConfigurationProperties(ZooProperties.class)
public class ZooConfig {

    @Bean
    public CuratorFramework curatorClient(ZooProperties properties) {
        ExponentialBackoffRetry retry = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.newClient(properties.getHost() + ":" + properties.getPort(), retry);
        client.start();
        return client;
    }
}
