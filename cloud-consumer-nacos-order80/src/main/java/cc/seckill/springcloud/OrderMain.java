package cc.seckill.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * description: OrderMain <br>
 * date: 2022/4/19 20:15 <br>
 * author: hq <br>
 * version: 1.0 <br>
 */
@SpringBootApplication
@EnableDiscoveryClient
public class OrderMain {
    public static void main(String[] args) {
        SpringApplication.run(OrderMain.class, args);
    }
}
