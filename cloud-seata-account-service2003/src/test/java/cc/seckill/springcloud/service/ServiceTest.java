package cc.seckill.springcloud.service;

import cc.seckill.springcloud.AccountMain;
import cc.seckill.springcloud.config.EnvironmentVariableInit;
import cc.seckill.springcloud.dao.AccountMapper;
import cc.seckill.springcloud.domain.Account;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * description: ServiceTest <br>
 * date: 2022/5/7 16:24 <br>
 * author: hq <br>
 * version: 1.0 <br>
 */
@SpringBootTest(classes = {AccountMain.class})
public class ServiceTest{

    @Resource
    private AccountService accountService;

    @Resource
    private AccountMapper accountMapper;

    @BeforeAll
    public static void init() {
        EnvironmentVariableInit.init();
    }

    @Test
    public void test() {
        accountService.decrease(1L, new BigDecimal(100));
        QueryWrapper<Account> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(Account::getUserId, 1L);
        Account account = accountMapper.selectOne(queryWrapper);
        System.out.println(account.getTotal());
    }
}
