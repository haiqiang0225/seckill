package cc.seckill.springcloud.service;

import cc.seckill.springcloud.entities.Result;
import cc.seckill.springcloud.service.impl.PaymentHystrixService;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * description: PaymentFeignService <br>
 * date: 2022/4/13 10:53 <br>
 * author: hq <br>
 * version: 1.0 <br>
 */
@Component
@FeignClient(name = "CLOUD-PAYMENT-SERVICE", fallback = PaymentHystrixService.class)
public interface PaymentFeignService {

    @GetMapping(value = "/payment/get/{id}")
    Result getPayment(@PathVariable("id") Long id);

    @GetMapping(value = "/payment/feign/timeout")
    String paymentFeignTimeout();

    @GetMapping(value = "/payment/hyx/get/{id}")
    String paymentInfoOK(@PathVariable("id") Long id);

    @GetMapping(value = "/payment/hyx/timeout/get/{id}")
    String paymentInfoTimeout(@PathVariable("id") Long id);
}
