package cc.seckill.springcloud.service.impl;

import cc.seckill.springcloud.dao.PaymentMapper;
import cc.seckill.springcloud.entities.Payment;
import cc.seckill.springcloud.service.PaymentService;
import cn.hutool.core.util.IdUtil;
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
                    value = "5000")
    })
    public String paymentInfoTimeout(Long id) {
        // 模拟异常
//        if (true) {
//            throw new RuntimeException();
//        }
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "线程: " + Thread.currentThread().getName() + " payment id" + id;
    }

    @Override
    @HystrixCommand(fallbackMethod = "paymentCircuitBreakerHandler", commandProperties = {
            @HystrixProperty(name = "circuitBreaker.enabled", value = "true"),  // 开启断路器
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value =
                    "10"), // 请求次数
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds", value =
                    "10000"), // 时间窗口
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value =
                    "60"),  // 失败率到达多少后断开
    })
    public String paymentCircuitBreaker(Long id) {
        if (id < 0) {
            throw new RuntimeException("id negative");
        }
        String serialNumber = IdUtil.simpleUUID();
        return Thread.currentThread().getName() + "\t" + "调用成功, id = " + 1;
    }

    public String paymentCircuitBreakerHandler(Long id) {
        return "break!";
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
