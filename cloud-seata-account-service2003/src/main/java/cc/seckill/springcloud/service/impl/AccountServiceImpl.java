package cc.seckill.springcloud.service.impl;

import cc.seckill.springcloud.domain.Account;
import cc.seckill.springcloud.dao.AccountMapper;
import cc.seckill.springcloud.service.AccountService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * description: AccountServiceImpl <br>
 * date: 2022/5/7 16:11 <br>
 * author: hq <br>
 * version: 1.0 <br>
 */
@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    @Resource
    private AccountMapper accountMapper;

    @Override
    public void decrease(Long userId, BigDecimal money) {
        log.info("------->account-service 中扣减账户余额开始 ");
        QueryWrapper<Account> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(Account::getUserId, userId);

        try {
            // 模拟超时异常 调用方Feign超时时间设置的是1s所以一定会报错
            log.info("开始sleep");
            TimeUnit.SECONDS.sleep(50);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info("{} awake, userId={}", Thread.currentThread().getName(), userId);
        Account account = accountMapper.selectOne(queryWrapper);
        account.setUsed(account.getUsed().add(money));
        account.setResidue(account.getResidue().subtract(money));

        accountMapper.updateById(account);

        log.info("------->account-service 中扣减账户余额结束 ");
    }
}
