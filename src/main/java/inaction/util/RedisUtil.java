package inaction.util;

import io.lettuce.core.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author hundanli
 * @version 1.0.0
 * @date 2020/9/22 16:01
 */
@Component
public class RedisUtil {

    @Autowired
    RedisClient redisClient;

    public Long hincrby(String key, String field, long l) {

        return null;
    }
}
