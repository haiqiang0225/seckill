package cc.seckill.springcloud.service;

import cc.seckill.springcloud.entities.Payment;

/**
 * description: PaymentService <br>
 * date: 2022/4/8 18:34 <br>
 * author: hq <br>
 * version: 1.0 <br>
 */
public interface PaymentService {
    int create(Payment payment);

    Payment getPaymentById(Long id);

    String paymentInfoOk(Long id);

    String paymentInfoTimeout(Long id);

}
