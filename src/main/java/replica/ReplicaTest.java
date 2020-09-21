package replica;

import io.lettuce.core.ReadFrom;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.masterreplica.MasterReplica;
import io.lettuce.core.masterreplica.StatefulRedisMasterReplicaConnection;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2020/9/7 19:43
 */
public class ReplicaTest {


    @Test
    void baseReplica() {
        RedisClient redisClient = RedisClient.create();
        List<RedisURI> nodes = Arrays.asList(RedisURI.create("redis://localhost:6379/0"),
                RedisURI.create("redis://localhost:6380/0"),
                RedisURI.create("redis://localhost:6381/0"));
        StatefulRedisMasterReplicaConnection<String, String> replicaConnection =
                MasterReplica.connect(redisClient, StringCodec.UTF8, nodes);

        // 设置读策略，优先从主节点读取数据
        replicaConnection.setReadFrom(ReadFrom.MASTER_PREFERRED);

        RedisCommands<String, String> redisCommands = replicaConnection.sync();
        redisCommands.flushall();
        redisCommands.set("replica", "hello redis");
        String replica = redisCommands.get("replica");
        Assertions.assertEquals("hello redis", replica);

        replicaConnection.close();
        redisClient.shutdown();
    }

}
