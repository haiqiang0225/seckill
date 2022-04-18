package cc.seckill.springcloud;

import org.springframework.boot.test.context.SpringBootTest;

import java.time.ZonedDateTime;

/**
 * description: BaseTest <br>
 * date: 2022/4/18 20:06 <br>
 * author: hq <br>
 * version: 1.0 <br>
 */
@SpringBootTest(classes = GatewayMain.class)
public class BaseTest {

    public static void main(String[] args) {
        ZonedDateTime dateTime = ZonedDateTime.now();
        System.out.println(dateTime);
    }
}
