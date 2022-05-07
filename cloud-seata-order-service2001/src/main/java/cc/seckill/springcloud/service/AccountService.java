package cc.seckill.springcloud.service;

import cc.seckill.springcloud.entities.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

/**
 * description: AccountService <br>
 * date: 2022/5/7 09:49 <br>
 * author: hq <br>
 * version: 1.0 <br>
 */
@FeignClient(value = "seata-account-service")
@Component
public interface AccountService {

    @PostMapping("/account/decrease")
    Result decrease(@RequestParam("userId") Long userId,
                    @RequestParam("money") BigDecimal money);
}
