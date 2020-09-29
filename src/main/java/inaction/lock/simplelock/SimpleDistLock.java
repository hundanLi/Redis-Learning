package inaction.lock.simplelock;

import io.lettuce.core.RedisClient;
import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.TimeUnit;


/**
 * @author hundanli
 * @version 1.0.0
 * @date 2020/9/23 15:12
 */
@Slf4j
public class SimpleDistLock {

    private final String name;
    private static final String OK = "OK";
    public int timeout;
    private Watchdog watchdog;
    private final RedisClient redisClient;
    private final String uuid;

    public SimpleDistLock(String name, RedisClient redisClient) {
        this(name, 30, redisClient);
    }

    public SimpleDistLock(String name, int timeout, RedisClient redisClient) {
        this.name = name;
        this.timeout = timeout;
        this.redisClient = redisClient;
        this.uuid = UUID.randomUUID().toString();
    }

    public boolean lock() {
        if (tryLock()) {
            log.info("获取锁成功! Thread-uuid={}", uuid);
            // 创建线程执行延时逻辑
            this.watchdog = new Watchdog(this);
            this.watchdog.start();
            return true;
        }
        log.warn("获取锁失败!");
        return false;
    }

    private boolean tryLock() {
        try (StatefulRedisConnection<String, String> connect = redisClient.connect()) {
            RedisCommands<String, String> commands = connect.sync();
            SetArgs setArgs = SetArgs.Builder.ex(timeout).nx();
            return OK.equals(commands.set(name, uuid, setArgs));
        } catch (Exception e) {
            log.error("tryLock fail: {}", e.getLocalizedMessage());
        }
        return false;
    }

    public boolean release() {
        // 终止watchdog线程
        this.watchdog.stop();

        String script =
                "if (redis.call('get', KEYS[1]) == ARGV[1]) " +
                        "then return redis.call('del', KEYS[1]) " +
                        "else return 0 " +
                        "end";
        try (StatefulRedisConnection<String, String> connect = redisClient.connect()) {
            RedisCommands<String, String> commands = connect.sync();
            String[] keys = {name};
            String[] argv = {uuid};
            long del = commands.eval(script, ScriptOutputType.INTEGER, keys, argv);
            if (del == 1) {
                log.info("释放锁成功，Thread-uuid={}", uuid);
                return true;
            }
            log.info("释放锁失败，Thread-uuid={}", uuid);
            return false;
        } catch (Exception e) {
            log.error("lock release fail: {}", e.getLocalizedMessage());
        }
        return false;

    }

    public boolean delay() {
        try (StatefulRedisConnection<String, String> connect = redisClient.connect()) {
            RedisCommands<String, String> commands = connect.sync();
            SetArgs setArgs = SetArgs.Builder.ex(timeout).xx();
            return OK.equals(commands.set(name, uuid, setArgs));
        } catch (Exception e) {
            log.error("delay lock fail: {}", e.getLocalizedMessage());
        }
        return false;
    }

    @Data
    static class Watchdog {
        private Thread thread;
        private Worker worker;

        public Watchdog(SimpleDistLock simpleDistLock) {
            this.worker = new Worker(simpleDistLock);
            //noinspection AlibabaAvoidManuallyCreateThread
            this.thread = new Thread(worker);
        }

        void start() {
            this.thread.setDaemon(true);
            this.thread.start();
        }

        void stop() {
            this.thread.interrupt();
            this.worker.stop();
        }
    }

    static class Worker implements Runnable {
        private volatile boolean toStop;
        private final SimpleDistLock simpleDistLock;

        public Worker(SimpleDistLock simpleDistLock) {
            this.simpleDistLock = simpleDistLock;
            this.toStop = false;
        }

        public void stop() {
            this.toStop = true;
        }

        @Override
        public void run() {
            log.info("Watchdog开始运行...");
            try {
                do {
                    int sleep = simpleDistLock.timeout / 3;
                    sleep = sleep > 0 ? sleep : 1;
                    TimeUnit.SECONDS.sleep(sleep);
                    boolean delay = simpleDistLock.delay();
                    if (delay) {
                        log.info("分布式锁延时成功！");
                    } else {
                        log.error("分布式锁延时失败！");
                        break;
                    }
                } while (!toStop);
                // 告警通知，提醒人工处理，如何迫使前台线程终止？
            } catch (InterruptedException e) {
                log.warn("分布式锁延时线程被终止：ThreadId={}", simpleDistLock.uuid);
            } finally {
                log.info("Watchdog结束运行...");
            }
        }

    }

}
