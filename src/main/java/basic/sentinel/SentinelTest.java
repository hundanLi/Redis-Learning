package basic.sentinel;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.sentinel.api.StatefulRedisSentinelConnection;
import io.lettuce.core.sentinel.api.sync.RedisSentinelCommands;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2020/9/14 19:42
 */
public class SentinelTest {

    @Test
    void sentinelTest() {
//        RedisURI redisuri = RedisURI.create("redis://ubuntu.wsl:26379");
//        RedisClient redisClient = RedisClient.create(redisuri);
        RedisClient redisClient = RedisClient.create("redis-basic.sentinel://localhost:26379,localhost:26380,localhost:26381/0#mymaster");

        StatefulRedisSentinelConnection<String, String> connectSentinel = redisClient.connectSentinel();
        // 获取sentinel命令接口
        RedisSentinelCommands<String, String> sentinelCommands = connectSentinel.sync();
        // 发送sentinel命令，相当于命令: basic.sentinel master mymaster
        Map<String, String> mymaster = sentinelCommands.master("mymaster");

        System.out.println(mymaster);

    }

    @Test
    void commandTest() {
        // 发送redis命令
        RedisClient redisClient = RedisClient.create("redis-basic.sentinel://localhost:26379,localhost:26380,localhost:26381/0#mymaster");
        StatefulRedisConnection<String, String> connect = redisClient.connect();
        RedisCommands<String, String> redisCommands = connect.sync();
        redisCommands.set("hello", "world");

        Assertions.assertEquals("world", redisCommands.get("hello"));
    }

}
