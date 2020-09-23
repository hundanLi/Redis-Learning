package basic.command;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2020/9/4 19:58
 */
public class SetTest {
    RedisClient redisClient;
    RedisCommands<String, String> redisCommands;
    final String key = "set";
    final String key1 = "set1", key2 = "set2";

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
    public void saddspopsrem() {
        redisCommands.sadd(key, "m1", "m2", "m3");
        Assertions.assertEquals(3, redisCommands.scard(key));


        Long srem = redisCommands.srem(key, "m2");
        Assertions.assertEquals(2, redisCommands.scard(key));

        String spop = redisCommands.spop(key);
        Assertions.assertFalse(redisCommands.sismember(key, spop));

    }

    @Test
    public void smembers() {
        redisCommands.sadd(key, "m1", "m2", "m3");
        String member = redisCommands.srandmember(key);
        Assertions.assertTrue(redisCommands.sismember(key, member));

        Set<String> smembers = redisCommands.smembers(key);
        Assertions.assertEquals(3, smembers.size());
        smembers.forEach(System.out::println);
    }


    @Test
    public void setinteract() {
        redisCommands.sadd(key1, "m1", "m2", "m3");
        redisCommands.sadd(key2, "m2", "m3", "m4");

        Set<String> sinter = redisCommands.sinter(key1, key2);
        Assertions.assertEquals(2, sinter.size());
        System.out.println(sinter);

        Set<String> sunion = redisCommands.sunion(key1, key2);
        Assertions.assertEquals(4, sunion.size());
        System.out.println(sunion);

        Set<String> sdiff = redisCommands.sdiff(key1, key2);
        Assertions.assertEquals(1, sdiff.size());
        System.out.println(sdiff);

    }
}
