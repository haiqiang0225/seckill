package cc.seckill.springcloud.controller;

import cc.seckill.springcloud.entities.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


/**
 * description: PaymentController <br>
 * date: 2022/4/19 19:49 <br>
 * author: hq <br>
 * version: 1.0 <br>
 */
@RestController
@Slf4j
public class PaymentController {
    @Value("${server.port}")
    private String serverPort;

    @GetMapping("/payment/nacos/get/{id}")
    public Result getPayment(@PathVariable("id") Long id) {
        Result result = new Result();
        result.put("code", "200");
        result.put("data", id);
        result.put("port", serverPort);
        return result;
    }

}
