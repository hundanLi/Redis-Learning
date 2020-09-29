package inaction.lock.simplelock;

import io.lettuce.core.RedisClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2020/9/27 11:23
 */
@RestController
@Slf4j
@RequestMapping("distlock")
public class BizController {

    @Autowired
    private RedisClient redisClient;

    @GetMapping("/simple")
    public String biz() throws InterruptedException {
        SimpleDistLock simpleDistLock = new SimpleDistLock("simple-distlock", redisClient);
        boolean lock = simpleDistLock.lock();

        if (lock) {
            try {
                // 模拟业务逻辑
                log.info("执行业务逻辑...");
                TimeUnit.SECONDS.sleep(1);
                log.info("业务逻辑执行成功！");

                return "执行完成！";
            } finally {
                simpleDistLock.release();
            }
        } else {
            return "获取锁失败！";
        }
    }


}