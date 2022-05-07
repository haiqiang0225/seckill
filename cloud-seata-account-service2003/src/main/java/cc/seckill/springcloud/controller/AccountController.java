package cc.seckill.springcloud.controller;

import cc.seckill.springcloud.service.AccountService;
import cc.seckill.springcloud.entities.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * description: AccountController <br>
 * date: 2022/5/7 16:20 <br>
 * author: hq <br>
 * version: 1.0 <br>
 */

@RestController
public class AccountController {
    @Resource
    private AccountService accountService;


    @RequestMapping(value = "/account/decrease")
    public Result decrease(@RequestParam("userId") Long userId,
                           @RequestParam("money") BigDecimal money) {
        accountService.decrease(userId, money);
        return new Result()
                .code(200)
                .msg("扣减余额成功");
    }
}
