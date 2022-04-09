package cc.seckill.springcloud.service.impl;

import cc.seckill.springcloud.dao.PaymentMapper;
import cc.seckill.springcloud.entities.Payment;
import cc.seckill.springcloud.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * description: PaymentServiceImpl <br>
 * date: 2022/4/8 18:35 <br>
 * author: hq <br>
 * version: 1.0 <br>
 */
@Service
public class PaymentServiceImpl implements PaymentService {

    private PaymentMapper paymentMapper;


    @Override
    public int create(Payment payment) {
        return paymentMapper.insert(payment);
    }

    @Override
    public Payment getPaymentById(Long id) {
        return paymentMapper.selectById(id);
    }


    /***************       setters       ***************/
    @Autowired
    public void setPaymentMapper(PaymentMapper paymentMapper) {
        this.paymentMapper = paymentMapper;
    }
}
