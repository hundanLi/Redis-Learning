package sentinel;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.sentinel.api.StatefulRedisSentinelConnection;
import io.lettuce.core.sentinel.api.sync.RedisSentinelCommands;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static java.awt.SystemColor.info;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2020/9/14 19:42
 */
public class SentinelTest {

    @Test
    void sentinelTest() {
        RedisURI redisuri = RedisURI.create("redis://ubuntu.wsl:26379");
        RedisClient redisClient = RedisClient.create(redisuri);
        StatefulRedisSentinelConnection<String, String> connectSentinel = redisClient.connectSentinel();
        // 获取sentinel命令接口
        RedisSentinelCommands<String, String> sentinelCommands = connectSentinel.sync();
        // 发送sentinel命令，相当于命令: sentinel master mymaster
        Map<String, String> mymaster = sentinelCommands.master("mymaster");

        System.out.println(mymaster);

    }


    @Test
    void commandTest() {
        // 执行redis命令
        RedisURI redisUri = RedisURI.Builder.sentinel("ubuntu.wsl", "mymaster").build();
        RedisClient redisClient = RedisClient.create(redisUri);
        StatefulRedisConnection<String, String> redisConnection = redisClient.connect();
        RedisCommands<String, String> sync = redisConnection.sync();
        sync.set("hello", "world");
        Assertions.assertEquals("world", sync.get("hello"));

    }

}
