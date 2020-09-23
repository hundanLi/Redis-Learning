package basic.command;

import io.lettuce.core.KeyValue;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2020/9/4 15:55
 */
public class StringTest {

    RedisClient redisClient;
    RedisCommands<String, String> redisCommands;

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
    public void setget() {
        redisCommands.set("foo", "bar");
        String foo = redisCommands.get("foo");
        Assertions.assertEquals("bar", foo);
        boolean setnx = redisCommands.setnx("foo", "bar");
        Assertions.assertFalse(setnx);
    }

    @Test
    public void msetmget() {
        HashMap<String, String> map = new HashMap<>(2);
        map.put("foo", "bar");
        map.put("hello", "word");
        redisCommands.mset(map);
        List<KeyValue<String, String>> mget = redisCommands.mget("foo", "hello");
        mget.forEach(System.out::println);

        map.put("hi", "redis");
        boolean msetnx = redisCommands.msetnx(map);
        Assertions.assertFalse(msetnx);

    }

    @Test
    public void setex() {
        redisCommands.setex("foo", 10, "bar");
        long foo = redisCommands.ttl("foo");
        Assertions.assertEquals(10, foo);
    }

    @Test
    public void incr() {
        redisCommands.set("foo", "1");
        redisCommands.incr("foo");
        String foo = redisCommands.get("foo");
        Assertions.assertEquals("2", foo);
        redisCommands.incrby("foo", 2);
        foo = redisCommands.get("foo");
        Assertions.assertEquals("4", foo);
    }

    @Test
    public void decr() {
        redisCommands.set("foo", "10");
        redisCommands.decr("foo");
        String foo = redisCommands.get("foo");
        Assertions.assertEquals("9", foo);
        redisCommands.decrby("foo", 2);
        foo = redisCommands.get("foo");
        Assertions.assertEquals("7", foo);
    }


    @Test
    public void append() {
        redisCommands.setnx("foo", "foo");
        redisCommands.append("foo", "bar");
        String foo = redisCommands.get("foo");
        Assertions.assertEquals("foobar", foo);
    }

    @Test
    public void strlen() {
        redisCommands.setnx("foo", "foo");
        long l = redisCommands.strlen("foo");
        Assertions.assertEquals(3, l);
    }

}
