package cc.seckill.springcloud.controller;

import cc.seckill.springcloud.entities.Result;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * description: OrderNacosController <br>
 * date: 2022/4/19 20:19 <br>
 * author: hq <br>
 * version: 1.0 <br>
 */
@RestController
@Slf4j
public class OrderNacosController {

    @Resource
    private RestTemplate restTemplate;

    @Value("${service-url.nacos-user}")
    private String serverURL;


    @GetMapping("/consumer/payment/nacos/get/{id}")
    @SentinelResource()
    public Result paymentInfo(@PathVariable("id") Long id) {
        return restTemplate.getForObject(serverURL + "/payment/nacos/get/" + id,
                Result.class);
    }

}
