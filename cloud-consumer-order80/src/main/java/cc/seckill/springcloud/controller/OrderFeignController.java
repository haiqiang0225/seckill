package cc.seckill.springcloud.controller;

import cc.seckill.springcloud.entities.Result;
import cc.seckill.springcloud.service.PaymentFeignService;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
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
@DefaultProperties(defaultFallback = "paymentGlobalFallbackHyxHandler")
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
    }

//    @HystrixCommand(fallbackMethod = "paymentInfoTimeoutHyxHandler", commandProperties = {
//            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",
//                    value = "2000")
//    })
    @HystrixCommand
    @GetMapping(value = "/consumer/payment/hyx/timeout/get/{id}")
    String paymentInfoTimeout(@PathVariable("id") Long id) {
        return paymentFeignService.paymentInfoTimeout(id);
    }


    public String paymentInfoTimeoutHyxHandler(@PathVariable("id") Long id) {
        return "80: 支付系统繁忙,请稍后调用";
    }

    public String paymentGlobalFallbackHyxHandler() {
        return "80: 出错啦!请稍后再试.";
    }

}
