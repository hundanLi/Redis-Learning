package inaction.counter.controller;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * API访问次数统计
 *
 * @author hundanli
 * @version 1.0.0
 * @date 2020/9/22 13:38
 */
@Slf4j
@RestController
public class AccessCounter {

    @Autowired
    private RedisClient redisClient;

    private static final String KEY = "access-count";

    @PostMapping("/index")
    public long index(HttpServletRequest request) {
        try (StatefulRedisConnection<String, String> connect = redisClient.connect()) {
            RedisCommands<String, String> commands = connect.sync();
            String uri = request.getRequestURI();
            Long count = commands.hincrby(KEY, uri, 1);
            log.info("index count increases: count = {}", count);
            return count;
        } catch (Exception e) {
            log.error("index count increase fail: ", e);
        }
        return -1;
    }

    @PostMapping("/detail")
    public long detail(HttpServletRequest request) {
        try (StatefulRedisConnection<String, String> connect = redisClient.connect()) {
            RedisCommands<String, String> commands = connect.sync();
            String uri = request.getRequestURI();
            Long count = commands.hincrby(KEY, uri, 1);
            log.info("detail count increases: count = {}", count);
            return count;
        } catch (Exception e) {
            log.error("detail count increase fail: ", e);
        }
        return -1;
    }
}
