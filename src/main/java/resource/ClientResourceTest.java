package resource;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2020/9/20 15:08
 */
public class ClientResourceTest {

    @Test
    void resourceTest() {

        // 默认资源配置
        ClientResources defaultResources = ClientResources.create();

        // 自定义线程池大小
        ClientResources resources = DefaultClientResources.builder()
                .ioThreadPoolSize(4)
                .computationThreadPoolSize(4)
                .build();

        RedisClient redisClient = RedisClient.create(resources, "redis://localhost:6379/0");
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> sync = connection.sync();
        sync.set("foo", "bar");
        Assertions.assertEquals("bar", sync.get("foo"));

        // 使用完毕需要关闭资源
        defaultResources.shutdown();
        resources.shutdown();
    }
}
