package cc.seckill.springcloud.dao;

import cc.seckill.springcloud.entities.Payment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * description: PaymentMapper <br>
 * date: 2022/4/8 15:17 <br>
 * author: hq <br>
 * version: 1.0 <br>
 */
@Mapper
public interface PaymentMapper extends BaseMapper<Payment> {
}
