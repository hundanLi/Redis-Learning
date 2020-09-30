package inaction.lock.zookeeper;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2020/9/29 15:51
 */
@RestController
@RequestMapping("/distlock")
@Slf4j
public class ZooDistLockController {

    @Autowired
    private CuratorFramework client;

    @GetMapping("/zoo")
    public String biz() throws Exception {

        InterProcessMutex lock = new InterProcessMutex(client, "/locks/sample");
        if (lock.acquire(1000, TimeUnit.MILLISECONDS)) {
            log.info("获取锁成功");
            try {
                log.info("执行业务逻辑...");
                TimeUnit.SECONDS.sleep(1);
                log.info("业务执行完毕！");
                return "成功";
            }finally {
                lock.release();
            }
        }
        log.warn("获取锁失败");
        return "失败";
    }
}
