package inaction.lock;

import io.lettuce.core.RedisClient;
import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;


/**
 * @author hundanli
 * @version 1.0.0
 * @date 2020/9/23 15:12
 */
@Slf4j
@Component
public class LockHelper {

    private static final String KEY = "lock";
    private static final String OK = "OK";
    public static final int TIMEOUT = 10;

    @Autowired
    RedisClient redisClient;

    public String lock() {
        String threadId = UUID.randomUUID().toString();
        if (tryLock(threadId)) {
            log.info("获取锁成功! Thread-uuid={}", threadId);
            return threadId;
        }
        log.warn("获取锁失败!");
        return null;
    }

    private boolean tryLock(String threadId) {
        try (StatefulRedisConnection<String, String> connect = redisClient.connect()) {
            RedisCommands<String, String> commands = connect.sync();
            SetArgs setArgs = SetArgs.Builder.ex(TIMEOUT).nx();
            return OK.equals(commands.set(KEY, threadId, setArgs));
        } catch (Exception e) {
            log.error("tryLock fail: {}", e.getLocalizedMessage());
        }
        return false;
    }

    public boolean release(String threadId) {
        if (threadId == null) {
            return false;
        }
        String script =
                "if (redis.call('get', KEYS[1]) == ARGV[1])\n" +
                        "    then return redis.call('del', KEYS[1])\n" +
                        "else return 0\n" +
                        "end";
        try (StatefulRedisConnection<String, String> connect = redisClient.connect()) {
            RedisCommands<String, String> commands = connect.sync();
            String[] keys = {KEY};
            String[] argv = {threadId};
            long del = commands.eval(script, ScriptOutputType.INTEGER, keys, argv);
            if (del == 1) {
                log.info("释放锁成功，Thread-uuid={}", threadId);
                return true;
            }
            log.info("释放锁失败，Thread-uuid={}", threadId);
            return false;
        } catch (Exception e) {
            log.error("lock release fail: {}", e.getLocalizedMessage());
        }
        return false;

    }

    public boolean delay(String threadId) {
        try (StatefulRedisConnection<String, String> connect = redisClient.connect()) {
            RedisCommands<String, String> commands = connect.sync();
            SetArgs setArgs = SetArgs.Builder.ex(TIMEOUT).xx();
            return OK.equals(commands.set(KEY, threadId, setArgs));
        } catch (Exception e) {
            log.error("delay lock fail: {}", e.getLocalizedMessage());
        }
        return false;
    }
}
