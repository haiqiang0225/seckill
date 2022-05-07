package cc.seckill.springcloud.service;

import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * description: AccountService <br>
 * date: 2022/5/7 16:10 <br>
 * author: hq <br>
 * version: 1.0 <br>
 */
public interface AccountService {
    void decrease(@RequestParam("userId") Long userId,
                  @RequestParam("money") BigDecimal money);
}
