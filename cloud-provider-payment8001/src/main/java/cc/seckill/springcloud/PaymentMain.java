package cc.seckill.springcloud;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * description: PaymentMain <br>
 * date: 2022/4/7 22:25 <br>
 * author: hq <br>
 * version: 1.0 <br>
 */
@SpringBootApplication
@MapperScan("cc.seckill.springcloud.dao")
public class PaymentMain {
    public static void main(String[] args) {
        System.setProperty("jasypt.encryptor.password", System.getenv("JASYPT_PASS"));
        SpringApplication.run(PaymentMain.class, args);
    }
}
