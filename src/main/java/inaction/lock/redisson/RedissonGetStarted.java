package inaction.lock.redisson;

import org.redisson.Redisson;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2020/9/28 18:03
 */
public class RedissonGetStarted {

    public static void main(String[] args) {
        // 创建配置类
        Config config = new Config();
        config.useSingleServer().setAddress("redis://localhost:6379");

        // 创建client
        RedissonClient redissonClient = Redisson.create(config);

        // 获取基于redis实现的标准java接口实现
        RMap<String, Object> rMap = redissonClient.getMap("my-map");
        rMap.put("hello", "inaction/lock/redisson");
        Object hello = rMap.get("hello");
        assert "inaction/lock/redisson".equals(hello);

        redissonClient.shutdown();
    }
}
