package inaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2020/9/22 11:40
 */
//@MapperScan(basePackages = "inaction.*.mapper")
@SpringBootApplication(scanBasePackages = "inaction.*", exclude = {DataSourceAutoConfiguration.class})
public class InActionApplication {
    public static void main(String[] args) {
        SpringApplication.run(InActionApplication.class, args);
    }
}
