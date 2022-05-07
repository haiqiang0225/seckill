package cc.seckill.springcloud.dao;

import cc.seckill.springcloud.domain.Account;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * description: AccountMapper <br>
 * date: 2022/5/7 16:08 <br>
 * author: hq <br>
 * version: 1.0 <br>
 */
@Mapper
public interface AccountMapper extends BaseMapper<Account> {
}
