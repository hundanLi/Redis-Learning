package basic.command;

import io.lettuce.core.Range;
import io.lettuce.core.RedisClient;
import io.lettuce.core.ScoredValue;
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
 * @date 2020/9/7 10:56
 */
public class SortedSetTest {
    RedisClient redisClient;
    RedisCommands<String, String> redisCommands;
    final String key = "sortedset";

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
    public void zaddzrem() {
        redisCommands.zadd(key, 1.0, "v1");
        redisCommands.zadd(key, 2.0, "v2");
        redisCommands.zadd(key, 0.5, "v3");
        redisCommands.zaddincr(key, 1.5, "v1");
        Assertions.assertEquals(3, redisCommands.zcard(key));

        redisCommands.zrem(key, "v2");
        Assertions.assertEquals(2, redisCommands.zcard(key));

    }

    @Test
    public void zcount() {
        redisCommands.zadd(key, 1.0, "v1");
        redisCommands.zadd(key, 2.0, "v2");
        redisCommands.zadd(key, 0.5, "v3");
        Assertions.assertEquals(3, redisCommands.zcard(key));
        Assertions.assertEquals(2, redisCommands.zcount(key, Range.create(1.0, 2.0)));

    }


    @Test
    public void zpop() {
        redisCommands.zadd(key, 1.0, "v1");
        redisCommands.zadd(key, 2.0, "v2");
        redisCommands.zadd(key, 0.5, "v3");
        Assertions.assertEquals("v3", redisCommands.zpopmin(key).getValue());
        Assertions.assertEquals("v2", redisCommands.zpopmax(key).getValue());
    }


    @Test
    public void zincrby() {
        redisCommands.zadd(key, 1.0, "v1");
        redisCommands.zincrby(key, 2.0, "v1");
        ScoredValue<String> scoredValue = redisCommands.zpopmin(key);
        Assertions.assertEquals(3.0, scoredValue.getScore());
    }


    @Test
    public void zrange() {
        redisCommands.zadd(key, 1.0, "v1");
        redisCommands.zadd(key, 2.0, "v2");
        redisCommands.zadd(key, 0.5, "v3");
        List<String> zrange = redisCommands.zrange(key, 1, 2);
        Assertions.assertEquals(Arrays.asList("v1", "v2"), zrange);
        List<String> zrangebyscore = redisCommands.zrangebyscore(key, Range.create(0, 1));
        Assertions.assertEquals(Arrays.asList("v3", "v1"), zrangebyscore);

        List<ScoredValue<String>> scoredValues = redisCommands.zrangeWithScores(key, 1, 2);
        System.out.println(scoredValues);

        List<String> zrevrange = redisCommands.zrevrange(key, 0, -1);
        System.out.println(zrevrange);

    }

    @Test
    public void zrank() {
        redisCommands.zadd(key, 1.0, "v1");
        redisCommands.zadd(key, 2.0, "v2");
        redisCommands.zadd(key, 0.5, "v3");
        long v2 = redisCommands.zrank(key, "v2");
        Assertions.assertEquals(2, v2);
    }


    @Test
    public void zscore() {
        redisCommands.zadd(key, 1.0, "v1");
        redisCommands.zadd(key, 2.0, "v2");
        Double zscore = redisCommands.zscore(key, "v2");
        Assertions.assertEquals(2.0, zscore);
    }
}
