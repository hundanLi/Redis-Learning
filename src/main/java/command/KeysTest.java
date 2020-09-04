package command;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2020/9/4 14:33
 */
public class KeysTest {

    RedisClient redisClient;
    RedisCommands<String, String> redisCommands;

    @Before
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
    public void keys() {
        // 插入数据
        redisCommands.set("hello", "world");
        redisCommands.set("foo", "bar");
        List<String> keys = redisCommands.keys("*");
        keys.forEach(System.out::println);
        keys = redisCommands.keys("he*");
        keys.forEach(System.out::println);

    }

    @Test
    public void exists() {
        redisCommands.set("foo", "bar");
        long exists = redisCommands.exists("foo");
        Assert.assertEquals(1, exists);
    }


    @Test
    public void type() {
        redisCommands.set("foo", "bar");
        String type = redisCommands.type("foo");
        Assert.assertEquals("string", type);

    }

    @Test
    public void expireTtlPersist() {
        redisCommands.set("foo", "bar");
        long foo = redisCommands.ttl("foo");
        Assert.assertEquals(-1, foo);
        redisCommands.expire("foo", 10);
        foo = redisCommands.ttl("foo");
        Assert.assertEquals(10, foo);
        redisCommands.persist("foo");
        foo = redisCommands.ttl("foo");
        Assert.assertEquals(-1, foo);

    }

    @Test
    public void del() {
        redisCommands.set("foo", "bar");
        redisCommands.del("foo");
        long foo = redisCommands.exists("foo");
        Assert.assertEquals(0, foo);
    }
}
