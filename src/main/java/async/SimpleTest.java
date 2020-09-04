package async;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.api.async.RedisAsyncCommands;

import java.util.concurrent.TimeUnit;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2020/9/4 13:42
 */
public class SimpleTest {
    public static void main(String[] args) throws InterruptedException {
        RedisClient redisClient = RedisClient.create("redis://localhost:6379/0");
        RedisAsyncCommands<String, String> asyncCommands = redisClient.connect().async();

        // set k-v
        RedisFuture<String> setFuture = asyncCommands.set("Hello", "Async");
        // 同步处理结果
        setFuture.thenAccept(s -> System.out.println("set result: " + s));

        // get k-v
        RedisFuture<String> getFuture = asyncCommands.get("Hello");
        // 异步处理，默认的执行线程池是ForkJoinPool.commonPool()
        getFuture.thenAcceptAsync(s -> System.out.println("get result: " + s));

        // 等待异步线程执行完成
        TimeUnit.SECONDS.sleep(1);

    }
}
