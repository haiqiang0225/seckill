package cc.seckill.springcloud.controller;

import cc.seckill.springcloud.TestBase;
import cc.seckill.springcloud.entities.Payment;
import cc.seckill.springcloud.entities.Result;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * description: PaymentControllerTest <br>
 * date: 2022/4/8 18:59 <br>
 * author: hq <br>
 * version: 1.0 <br>
 */
@SpringBootTest
public class PaymentControllerTest extends TestBase {

    @Autowired
    private PaymentController paymentController;


    @Test
    public void testCreate() {
        Payment payment = new Payment();
        payment.setSerial("xwfq1f3qfasvava");
        Result result = paymentController.create(payment);
        assert result != null;
        System.out.println("code : " + result.get("code"));
    }

}
