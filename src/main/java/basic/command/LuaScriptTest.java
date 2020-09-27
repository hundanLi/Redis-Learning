package basic.command;

import io.lettuce.core.RedisClient;
import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Year;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2020/9/25 20:23
 */
public class LuaScriptTest {
    RedisClient redisClient;
    RedisCommands<String, String> syncCommands;

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

    }


    @Test
    public void helloWorld() {
        String luaScript = "return {KEYS[1], KEYS[2], ARGV[1], ARGV[2]}";
        String[] keys = {"key1", "key2"};
        String[] values = {"value1", "value2"};
        Object eval = syncCommands.eval(luaScript, ScriptOutputType.MULTI, keys, values);
        System.out.println(eval);
    }


    @Test
    public void redisCommand() {
        String setScript = "return redis.call('set', KEYS[1], ARGV[1])";
        String[] keys = {"hello"};
        String[] values = {"lua"};
        Object set = syncCommands.eval(setScript, ScriptOutputType.VALUE, keys, values);
        Assertions.assertEquals("OK", set);

        String getScript = "return redis.call('get', KEYS[1])";
        keys = new String[]{"hello"};
        Object get = syncCommands.eval(getScript, ScriptOutputType.VALUE, keys);
        Assertions.assertEquals("lua", get);

        String delScript = "return redis.call('del', KEYS[1])";
        Object del = syncCommands.eval(delScript, ScriptOutputType.INTEGER, keys);
        Assertions.assertEquals(1L, del);

    }


}
