package cc.seckill.springcloud;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * description: TestBase <br>
 * date: 2022/4/8 15:26 <br>
 * author: hq <br>
 * version: 1.0 <br>
 */
@SpringBootTest
public class TestBase {

    @BeforeAll
    public static void init() {
        System.setProperty("jasypt.encryptor.password", System.getenv("JASYPT_PASS"));
    }
}
