package inaction;

import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2020/9/22 11:40
 */
@SpringBootApplication(scanBasePackages = "inaction.*", exclude = {DataSourceAutoConfiguration.class})
@EnableCreateCacheAnnotation
@EnableMethodCache(basePackages = "inaction")
public class InActionApplication {
    public static void main(String[] args) {
        SpringApplication.run(InActionApplication.class, args);
    }
}
