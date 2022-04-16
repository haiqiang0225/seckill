package cc.seckill.springcloud.service.impl;

import cc.seckill.springcloud.entities.Result;
import cc.seckill.springcloud.service.PaymentFeignService;
import org.springframework.stereotype.Component;

/**
 * description: PaymentHystrixService <br>
 * date: 2022/4/16 19:04 <br>
 * author: hq <br>
 * version: 1.0 <br>
 */
@Component
public class PaymentHystrixService implements PaymentFeignService {
    @Override
    public Result getPayment(Long id) {
        return Result.error();
    }

    @Override
    public String paymentFeignTimeout() {
        return "PaymentHystrixService: time out, wait !";
    }

    @Override
    public String paymentInfoOK(Long id) {
        return "PaymentHystrixService: ok";
    }

    @Override
    public String paymentInfoTimeout(Long id) {
        return "PaymentHystrixService: time out, wait !";
    }
}
