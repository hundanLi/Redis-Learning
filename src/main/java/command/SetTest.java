package command;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.jupiter.api.BeforeEach;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2020/9/4 19:58
 */
public class SetTest {
    RedisClient redisClient;
    RedisCommands<String, String> redisCommands;
    final String key = "set";

    @BeforeEach
    public void setup() {
        String url = "redis://localhost:6379/0";
        // 创建RedisClient实例
        redisClient = RedisClient.create(url);
        // 根据提供的url获取连接
        StatefulRedisConnection<String, String> redisConnection =
                redisClient.connect();
        // 获取同步API，用于执行redis命令
        redisCommands = redisConnection.sync();
        redisCommands.flushall();

    }
}
