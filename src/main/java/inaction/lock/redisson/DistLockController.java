package inaction.lock.redisson;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2020/9/28 18:59
 */
@RestController
@Slf4j
@RequestMapping("/distlock")
public class DistLockController {

    @Autowired
    private RedissonClient redisson;

    @GetMapping("/redisson-biz")
    public String biz() throws InterruptedException {

        RLock lock = redisson.getLock("dist-lock");
//        boolean res = lock.tryLock(100, 10, TimeUnit.SECONDS);
        boolean res = lock.tryLock();
        if (res) {
            log.info("获取锁成功");
            try {
                log.info("业务逻辑开始执行...");
                TimeUnit.SECONDS.sleep(1);
                log.info("业务逻辑执行完成!");
                return "获取锁成功";
            }finally {
                lock.unlock();
            }
        }
        log.warn("获取锁失败");
        return "获取锁失败";
    }
}
