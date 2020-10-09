package inaction.cache;

import com.github.benmanes.caffeine.cache.*;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2020/9/30 15:40
 */
public class CaffeineTest {

    @Test
    void crud() {
        // 构建缓存
        LoadingCache<String, String> cache = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .maximumSize(1000)
                .removalListener(new RemovalListener<String, String>() {
                    @Override
                    public void onRemoval(@Nullable String key, @Nullable String value, @NonNull RemovalCause cause) {
                        System.out.println("key => " + value + " 被移除！ 原因：" + cause.toString());
                    }
                })
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(@NonNull String key) throws Exception {
                        return key + "-";
                    }
                });

        // 查找缓存，如果缓存不存在则生成缓存元素,  如果无法生成则返回null
        String hello = cache.get("hello", new Function<String, String>() {
            @Override
            public String apply(String key) {
                return key + "-world";
            }
        });
        Assertions.assertEquals("hello-world", hello);

        // 添加或者更新一个缓存元素
        cache.put("hello", "caffeine");

        // 获取一个缓存元素，没有则返回null
        hello = cache.getIfPresent("hello");
        Assertions.assertEquals("caffeine", hello);

        cache.invalidate("hello");
        hello = cache.getIfPresent("hello");
        Assertions.assertNull(hello);

    }

    @Test
    void writer() {
        // CacheWriter能够使得所有的读和写操作都可以通过缓存向下传播，应用场景：写模式，分层缓存，同步监听器
        Cache<String, String> cache = Caffeine.newBuilder()
                .writer(new CacheWriter<String, String>() {
                    @Override
                    public void write(@NonNull String key, @NonNull String value) {
                        System.out.println("写到二级缓存...");
                    }

                    @Override
                    public void delete(@NonNull String key, @Nullable String value, @NonNull RemovalCause cause) {
                        System.out.println("移除二级缓存的数据...");
                    }
                })
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .build();

        cache.put("hello", "world");
        Assertions.assertEquals("world", cache.getIfPresent("hello"));
        cache.invalidate("hello");

    }

    @Test
    void statistics() {
        // 统计信息，创建缓存实例时调用recordStats()开启统计
        Cache<String, String> cache = Caffeine.newBuilder()
                .recordStats()
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .build();

        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            cache.put("" + random.nextInt(500), i + "");
        }

        int hitCount = 0;
        for (int i = 0; i < 100; i++) {
            String present = cache.getIfPresent("" + random.nextInt(500));
            if (present != null) {
                hitCount++;
            }
        }
        System.out.println("命中次数：" + hitCount);
        System.out.println("命中率：" + cache.stats().hitRate());
    }
}
