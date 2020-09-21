package cluster;

import io.lettuce.core.ReadFrom;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2020/9/19 20:17
 */
public class ClusterTest {

    RedisAdvancedClusterCommands<String, String> commands;
    private
    @BeforeEach
    void setup() {
        RedisClusterClient redisClusterClient = RedisClusterClient.create("redis://localhost:7000");
        StatefulRedisClusterConnection<String, String> connection = redisClusterClient.connect();
        // 优先从副本节点读数据
        connection.setReadFrom(ReadFrom.REPLICA_PREFERRED);
        commands = connection.sync();
    }

    /**
     * Using NodeSelection API to read all keys from all replicas
     */
    @Test
    void keys() {
        List<String> keys = commands.keys("*");
        System.out.println(keys);
        String foo = commands.get("foo");
        Assertions.assertEquals("bar", foo);

        String set = commands.set("hello", "redis");
        Assertions.assertEquals("OK", set);
        Assertions.assertEquals("redis", commands.get("hello"));

    }


}
