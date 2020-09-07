package command;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.TransactionResult;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2020/9/7 14:56
 */
public class TransactionTest {
    RedisClient redisClient;
    RedisCommands<String, String> syncCommands;
    RedisAsyncCommands<String, String> asyncCommands;

    @BeforeEach
    public void setup() {
        String url = "redis://localhost:6379/0";
        // 创建RedisClient实例
        redisClient = RedisClient.create(url);
        // 根据提供的url获取连接
        StatefulRedisConnection<String, String> redisConnection =
                redisClient.connect();

        // 获取同步API，用于执行redis命令
        syncCommands = redisConnection.sync();
        syncCommands.flushall();

        // 获取异步API
        asyncCommands = redisConnection.async();
        asyncCommands.flushall();
    }

    @Test
    public void sync() {

        // 标记开启事务
        syncCommands.multi();

        // 定义事务操作，并不实际执行，返回null
        String set = syncCommands.set("k1", "v1");
        Assertions.assertNull(set);
        set = syncCommands.set("k2", "v2");
        Assertions.assertNull(set);

        // 执行事务，返回结果
        TransactionResult result = syncCommands.exec();
        Assertions.assertFalse(result.wasDiscarded());
        result.forEach(r -> {
            Assertions.assertEquals("OK", r);
        });
    }


    @Test
    public void async() throws Exception {
        asyncCommands.multi();
        RedisFuture<String> set1 = asyncCommands.set("k1", "v1");
        RedisFuture<String> set2 = asyncCommands.set("k2", "v2");
        RedisFuture<TransactionResult> exec = asyncCommands.exec();
        Assertions.assertEquals(exec.get().get(0), set1.get());
        Assertions.assertEquals(exec.get().get(1), set2.get());

    }
}
