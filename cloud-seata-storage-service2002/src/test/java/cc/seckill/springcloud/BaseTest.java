package cc.seckill.springcloud;

import cc.seckill.springcloud.config.EnvironmentVariableInit;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * description: BaseTest <br>
 * date: 2022/5/7 14:57 <br>
 * author: hq <br>
 * version: 1.0 <br>
 */
@SpringBootTest
public class BaseTest {

    @BeforeAll
    public static void init() {
        EnvironmentVariableInit.init();
    }

}
