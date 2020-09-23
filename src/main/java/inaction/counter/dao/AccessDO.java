package inaction.counter.dao;

import lombok.Data;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2020/9/22 15:35
 */
@Data
public class AccessDO {
    private String path;
    private Long count;
}
