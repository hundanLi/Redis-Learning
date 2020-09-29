package inaction.lock.controller;

import inaction.lock.LockHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2020/9/27 11:23
 */
@RestController
@Slf4j
public class BizController {

    @Autowired
    private LockHelper lockHelper;

    @GetMapping("/biz")
    public String biz() throws InterruptedException {
        String uuid = lockHelper.lock();

        if (uuid != null) {
            try {
                // 创建线程执行延时逻辑
                Worker worker = new Worker(uuid, lockHelper);
                @SuppressWarnings("AlibabaAvoidManuallyCreateThread")
                Thread thread = new Thread(worker);
                thread.setDaemon(true);
                thread.start();

                // 模拟业务逻辑
                log.info("执行业务逻辑...");
                TimeUnit.SECONDS.sleep(12);
                log.info("业务逻辑执行成功！");
                thread.interrupt();
                worker.stop();
                return "执行完成！";
            } finally {
                lockHelper.release(uuid);
            }
        } else {
            return "获取锁失败！";
        }
    }


    public static class Worker implements Runnable {
        private volatile boolean toStop;
        private int delayTimes;
        private final String uuid;
        private final LockHelper lockHelper;

        public Worker(String uuid, LockHelper lockHelper) {
            this(uuid, 3, lockHelper);
        }

        public Worker(String uuid, int delayTimes, LockHelper lockHelper) {
            this.uuid = uuid;
            this.delayTimes = delayTimes;
            this.lockHelper = lockHelper;
            this.toStop = false;
        }

        public void stop() {
            this.toStop = true;
        }

        @Override
        public void run() {
            try {
                do {
                    TimeUnit.SECONDS.sleep(LockHelper.TIMEOUT - 1);
                    delayTimes--;
                    boolean delay = lockHelper.delay(this.uuid);
                    if (delay) {
                        log.info("分布式锁延时成功！");
                    }else {
                        log.error("分布式锁延时失败！");
                        break;
                    }
                } while (!toStop && delayTimes > 0);
                // 告警通知，提醒人工处理，如何迫使前台线程终止？
                log.warn("业务执行超时!");
                // 超时释放锁
                lockHelper.release(uuid);
            } catch (InterruptedException e) {
                log.warn("分布式锁延时线程被终止：ThreadId={}", uuid);
            } finally {
                this.stop();
            }
        }

    }
}