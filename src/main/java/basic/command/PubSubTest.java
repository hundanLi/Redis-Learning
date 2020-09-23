package basic.command;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.async.RedisPubSubAsyncCommands;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2020/9/7 13:54
 */
public class PubSubTest {
    RedisClient redisClient;
    String url = "redis://localhost:6379/0";
    StatefulRedisPubSubConnection<String, String> connectPubSub;
    final String channel = "channel";
    final String pattern = "pattern";

    @BeforeEach
    public void setup() {
        // 创建RedisClient实例
        redisClient = RedisClient.create(url);
        // 根据提供的url获取连接
        connectPubSub = redisClient.connectPubSub();

        connectPubSub.addListener(new RedisPubSubListener<String, String>() {
            @Override
            public void message(String channel, String message) {
                System.out.println("channel[" + channel + "]: " + message);
            }

            @Override
            public void message(String pattern, String channel, String message) {
                System.out.println("pattern[" + pattern + "] channel[" + channel + "]: " + message);
            }

            @Override
            public void subscribed(String channel, long count) {
                System.out.printf("channel[%s]收到第%d位订阅者\n", channel, count);
            }

            @Override
            public void psubscribed(String pattern, long count) {
                System.out.printf("pattern[%s]收到第%d位订阅者\n", pattern, count);
            }

            @Override
            public void unsubscribed(String channel, long count) {
                System.out.printf("channel[%s]收到第%d位订阅者的退订\n", channel, count);
            }

            @Override
            public void punsubscribed(String pattern, long count) {
                System.out.printf("pattern[%s]收到第%d位订阅者的退订\n", pattern, count);
            }
        });

    }


    @Test
    public void sync() {
        RedisPubSubCommands<String, String> pubSubCommands = connectPubSub.sync();
        pubSubCommands.psubscribe(pattern);
        pubSubCommands.subscribe(channel);

        RedisCommands<String, String> commands = redisClient.connect().sync();
        commands.publish(channel, "publish channel message");
        commands.publish(pattern, "publish pattern message");

        pubSubCommands.punsubscribe(pattern);
        pubSubCommands.unsubscribe(channel);

    }

    @Test
    public void async() {
        RedisPubSubAsyncCommands<String, String> pubSubCommands = connectPubSub.async();
        pubSubCommands.subscribe(channel);
        pubSubCommands.psubscribe(pattern);

        RedisCommands<String, String> commands = redisClient.connect().sync();
        commands.publish(channel, "publish async channel message");
        commands.publish(pattern, "publish async pattern message");

        pubSubCommands.punsubscribe(pattern);
        pubSubCommands.unsubscribe(channel);

    }
}
