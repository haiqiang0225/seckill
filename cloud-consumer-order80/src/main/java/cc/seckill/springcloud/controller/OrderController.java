package cc.seckill.springcloud.controller;

import cc.seckill.springcloud.entities.Payment;
import cc.seckill.springcloud.entities.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * description: OrderController <br>
 * date: 2022/4/11 10:15 <br>
 * author: hq <br>
 * version: 1.0 <br>
 */
@RestController
@Slf4j
// TODO: 修改REST风格接口
public class OrderController {
    //    public static final String PAYMENT_URL = "http://localhost:8001";
    public static final String PAYMENT_URL = "http://CLOUD-PAYMENT-SERVICE";

    @Resource
    private RestTemplate restTemplate;

    @GetMapping("/consumer/payment/create")
    public Result create(Payment payment) {
        return restTemplate.postForObject(PAYMENT_URL + "/payment/create", payment,
                Result.class);
    }

    @GetMapping("/consumer/payment/get/{id}")
    public Result getPayment(@PathVariable("id") Long id) {
        return restTemplate.getForObject(PAYMENT_URL + "/payment/get/" + id,
                Result.class);
    }

    @GetMapping("/consumer/payment/get/entity/{id}")
    public Result getPaymentEntity(@PathVariable("id") Long id) {
        ResponseEntity<Result> entity = restTemplate.getForEntity(PAYMENT_URL +
                        "/payment/get/" + id,
                Result.class);
        Result result = new Result();
        if (entity.getStatusCode().is2xxSuccessful()) {
            result.put("msg", "success");
        } else {
            result.put("code", 444);
            result.put("msg", "失败");
        }
        return result;
    }
}
