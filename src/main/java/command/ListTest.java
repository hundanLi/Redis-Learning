package command;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.Arrays;
import java.util.List;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2020/9/4 16:31
 */
public class ListTest {

    RedisClient redisClient;
    RedisCommands<String, String> redisCommands;

    final String key = "list";

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

    @Test
    public void pushpop() {
        redisCommands.lpush(key, "v1", "v2", "v3", "v4");
        List<String> lrange = redisCommands.lrange(key, 0, -1);
        Assertions.assertEquals(Arrays.asList("v4", "v3", "v2", "v1"), lrange);

        Assertions.assertEquals(redisCommands.lpop(key), "v4");
        Assertions.assertEquals(redisCommands.rpop(key), "v1");

        redisCommands.rpush(key, "v5");
        Assertions.assertEquals(redisCommands.rpop(key), "v5");


    }


    @Test
    public void lindex() {
        redisCommands.rpush(key, "v1", "v2", "v3", "v4");
        Assertions.assertEquals("v3", redisCommands.lindex(key, 2));
    }

    @Test
    public void insertset() {
        redisCommands.rpush(key, "v1", "v2", "v3", "v4");
        redisCommands.lset(key, 2, "v31");
        Assertions.assertEquals("v31", redisCommands.lindex(key, 2));

        redisCommands.linsert(key, false, "v2", "v21");
        Assertions.assertEquals("v21", redisCommands.lindex(key, 2));

    }

    @Test
    public void lrem() {
        redisCommands.rpush(key, "v1", "v1", "v2", "v3", "v4");
        redisCommands.lrem(key, 1, "v1");
        Assertions.assertEquals(Arrays.asList("v1", "v2", "v3", "v4"), redisCommands.lrange(key, 0, -1));
    }

    @Test
    public void ltrim() {
        redisCommands.rpush(key, "v1", "v2", "v3", "v4");
        redisCommands.ltrim(key, 1, 2);
        Assertions.assertEquals(Arrays.asList("v2", "v3"), redisCommands.lrange(key, 0, -1));
    }

    @Test
    public void llen() {
        redisCommands.rpush(key, "v1", "v2", "v3", "v4");
        Assertions.assertEquals(Long.valueOf(4), redisCommands.llen(key));
    }
}
