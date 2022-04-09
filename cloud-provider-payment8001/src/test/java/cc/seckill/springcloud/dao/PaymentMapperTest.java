package cc.seckill.springcloud.dao;

import cc.seckill.springcloud.PaymentMain;
import cc.seckill.springcloud.TestBase;
import cc.seckill.springcloud.entities.Payment;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * description: PaymentMapperTest <br>
 * date: 2022/4/8 15:26 <br>
 * author: hq <br>
 * version: 1.0 <br>
 */
@SpringBootTest(classes = PaymentMain.class)
public class PaymentMapperTest extends TestBase {


    @Autowired
    PaymentMapper mapper;


    @Test
    public void testMyBatisPlus() {
        Payment payment = new Payment();
        payment.setSerial("00ax2e1fanfkwafwazzsasfwfaw");

        int result = mapper.insert(payment);
        assert result > 0;
        System.out.println(result);
    }
}
