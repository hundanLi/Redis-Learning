package inaction.counter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import inaction.counter.dao.AccessDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author hundanli
 * @version 1.0.0
 * @date 2020/9/22 15:30
 */
@Mapper
public interface AccessMapper extends BaseMapper<AccessDO> {
}
