package command;

import io.lettuce.core.KeyValue;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2020/9/4 18:11
 */
public class HashTest {

    RedisClient redisClient;
    RedisCommands<String, String> redisCommands;
    final String key = "hash";

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
    public void hsetget() {
        redisCommands.hset(key, "f1", "v1");
        Assertions.assertEquals(redisCommands.hget(key, "f1"), "v1");
        Assertions.assertTrue(redisCommands.hsetnx(key, "f2", "v2"));
        Assertions.assertFalse(redisCommands.hsetnx(key, "f2", "v2"));

        HashMap<String, String> map = new HashMap<>();
        map.put("f3", "v3");
        map.put("f4", "v4");
        redisCommands.hmset(key, map);
        List<KeyValue<String, String>> keyValues = redisCommands.hmget(key, "f2", "f4");
        keyValues.forEach(System.out::println);
    }


    @Test
    public void hkeysvals() {
        HashMap<String, String> map = new HashMap<>(2);
        map.put("f3", "v3");
        map.put("f4", "v4");
        redisCommands.hmset(key, map);
        List<String> hkeys = redisCommands.hkeys(key);
        Assertions.assertEquals(hkeys, Arrays.asList("f3", "f4"));

        List<String> hvals = redisCommands.hvals(key);
        Assertions.assertEquals(hvals, Arrays.asList("v3", "v4"));

    }


    @Test
    public void hgetall() {
        HashMap<String, String> map = new HashMap<>(2);
        map.put("f3", "v3");
        map.put("f4", "v4");
        redisCommands.hmset(key, map);
        Map<String, String> hgetall = redisCommands.hgetall(key);
        Assertions.assertEquals(hgetall, map);

    }


    @Test
    public void hdelhexistshlen() {
        HashMap<String, String> map = new HashMap<>(2);
        map.put("f3", "v3");
        map.put("f4", "v4");
        redisCommands.hmset(key, map);
        Assertions.assertEquals(Long.valueOf(2), redisCommands.hlen(key));
        redisCommands.hdel(key, "f3");
        Assertions.assertFalse(redisCommands.hexists(key, "f3"));
        Assertions.assertEquals(Long.valueOf(1), redisCommands.hlen(key));

    }

    @Test
    public void hincrby() {
        redisCommands.hset(key, "f1", "10");
        redisCommands.hincrby(key, "f1", 2);
        Assertions.assertEquals("12", redisCommands.hget(key, "f1"));
    }

}
