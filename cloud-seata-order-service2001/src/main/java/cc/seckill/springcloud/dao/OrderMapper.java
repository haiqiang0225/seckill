package cc.seckill.springcloud.dao;

import cc.seckill.springcloud.domain.Order;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * description: OrderMapper <br>
 * date: 2022/5/7 09:33 <br>
 * author: hq <br>
 * version: 1.0 <br>
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
