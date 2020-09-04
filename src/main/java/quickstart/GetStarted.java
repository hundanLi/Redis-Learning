package quickstart;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2020/9/3 17:07
 */
public class GetStarted {

    public static void main(String[] args) {
        String url = "redis://localhost:6379/0";
        // 创建RedisClient实例
        RedisClient redisClient = RedisClient.create(url);
        // 根据提供的url获取连接
        StatefulRedisConnection<String, String> redisConnection =
                redisClient.connect();
        // 获取同步API，用于执行redis命令
        RedisCommands<String, String> redisCommands = redisConnection.sync();

        // 设置键-值
        String set = redisCommands.set("hello", "world");
        System.out.println(set);

        // 获取键值
        String get = redisCommands.get("hello");
        System.out.println(get);

        // 关闭资源
        redisConnection.close();
        redisClient.shutdown();

    }
}
