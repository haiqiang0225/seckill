package cc.seckill.springcloud.controller;

import cc.seckill.springcloud.entities.Result;
import cc.seckill.springcloud.service.PaymentFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * description: OrderFeignController <br>
 * date: 2022/4/13 12:10 <br>
 * author: hq <br>
 * version: 1.0 <br>
 */
@RestController
@Slf4j
public class OrderFeignController {
    @Resource
    private PaymentFeignService paymentFeignService;

    @GetMapping(value = "/consumer/feign/payment/get/{id}")
    public Result getPaymentById(@PathVariable("id") Long id) {
        return paymentFeignService.getPayment(id);
    }


    @GetMapping(value = "/consumer/feign/payment/timeout")
    public String paymentFeignTimeout() {
        // 默认等待一秒钟
        return paymentFeignService.paymentFeignTimeout();
    }


    @GetMapping(value = "/consumer/payment/hyx/get/{id}")
    String paymentInfoOK(@PathVariable("id") Long id) {
        return paymentFeignService.paymentInfoOK(id);
    };

    @GetMapping(value = "/consumer/payment/hyx/timeout/get/{id}")
    String paymentInfoTimeout(@PathVariable("id") Long id) {
        return paymentFeignService.paymentInfoTimeout(id);
    };
}
