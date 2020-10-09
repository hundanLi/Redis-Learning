package inaction.cache;

import lombok.Data;

import java.io.Serializable;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2020/10/9 14:51
 */
@Data
public class User implements Serializable {
    private Long id;
    private String name;
    private Integer age;
}
