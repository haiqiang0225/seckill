package cc.seckill.springcloud.config;

/**
 * description: EnvironmentVariableInit <br>
 * date: 2022/5/7 14:59 <br>
 * author: hq <br>
 * version: 1.0 <br>
 */
public class EnvironmentVariableInit {

    public static void init() {
        System.setProperty("jasypt.encryptor.password", System.getenv("JASYPT_PASS"));
    }
}
