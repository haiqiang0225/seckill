package cc.seckill.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.util.ArrayList;
import java.util.List;

/**
 * description: OrderMain <br>
 * date: 2022/4/11 10:13 <br>
 * author: hq <br>
 * version: 1.0 <br>
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@EnableCircuitBreaker
public class OrderMain {
    public static void main(String[] args) {
        SpringApplication.run(OrderMain.class, args);
        List<Integer> list = new ArrayList<>();
    }
}
