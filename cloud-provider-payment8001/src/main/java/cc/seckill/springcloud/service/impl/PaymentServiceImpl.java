package cc.seckill.springcloud.service.impl;

import cc.seckill.springcloud.dao.PaymentMapper;
import cc.seckill.springcloud.entities.Payment;
import cc.seckill.springcloud.service.PaymentService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

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

    @Override
    public String paymentInfoOk(Long id) {
        return "线程: " + Thread.currentThread().getName() + " payment id" + id;
    }

    @Override
    @HystrixCommand(fallbackMethod = "paymentInfoTimeoutHyxHandler", commandProperties = {
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",
                    value = "1000")
    })
    public String paymentInfoTimeout(Long id) {
        int i = 10 / 0;
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "线程: " + Thread.currentThread().getName() + " payment id" + id;
    }


    public String paymentInfoTimeoutHyxHandler(Long id) {
        return "线程: " + Thread.currentThread().getName() + " payment id" + id + "," +
                "出错🌶!等待超时!等会再来访问.o(╥﹏╥)o";
    }


    /***************       setters       ***************/
    @Autowired
    public void setPaymentMapper(PaymentMapper paymentMapper) {
        this.paymentMapper = paymentMapper;
    }
}
