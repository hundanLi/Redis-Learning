package inaction.cache;

import com.alicp.jetcache.anno.CacheInvalidate;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CacheUpdate;
import com.alicp.jetcache.anno.Cached;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2020/10/9 14:23
 */
@RestController
@RequestMapping("/cache")
public class JetCacheController {

    @PostMapping("/get")
    @Cached(name = "test", cacheType = CacheType.REMOTE, expire = 10, timeUnit = TimeUnit.MINUTES, key = "#user.id")
    public User getUser(@RequestBody User user) {
        System.out.println("find user...");
        user.setName("user1");
        user.setAge(11);
        return user;
    }


    @PostMapping("/update")
    @CacheUpdate(name = "test", key = "#user.id", value = "#user")
    public void updateStr(@RequestBody User user) {
        System.out.println("update user...");
    }

    @PostMapping("/remove")
    @CacheInvalidate(name = "test", key = "#user.id")
    public void removeStr(@RequestBody User user) {
        System.out.println("remove user...");
    }

}
