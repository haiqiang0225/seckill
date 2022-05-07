package cc.seckill.springcloud;

import cc.seckill.springcloud.config.EnvironmentVariableInit;
import io.seata.spring.annotation.datasource.EnableAutoDataSourceProxy;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * description: StorageMain <br>
 * date: 2022/5/7 14:32 <br>
 * author: hq <br>
 * version: 1.0 <br>
 */
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@EnableAutoDataSourceProxy
public class StorageMain {
    public static void main(String[] args) {
        EnvironmentVariableInit.init();
        SpringApplication.run(StorageMain.class, args);
    }
}
