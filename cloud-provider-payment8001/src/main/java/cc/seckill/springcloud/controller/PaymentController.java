package cc.seckill.springcloud.controller;

import cc.seckill.springcloud.entities.Payment;
import cc.seckill.springcloud.entities.Result;
import cc.seckill.springcloud.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * description: PaymentController <br>
 * date: 2022/4/8 18:45 <br>
 * author: hq <br>
 * version: 1.0 <br>
 */
@RestController
@Slf4j
public class PaymentController {
    private PaymentService paymentService;


    @PostMapping(value = "payment/create")
    public Result create(Payment payment) {
        if (payment == null || payment.getSerial() == null) {
            // 通知...
            log.error("未接收到第三方支付订单信息");
            return Result.error();
        }
        int res = paymentService.create(payment);
        log.info("支付信息: {}", payment);
        Result result = Result.ok();
        if (res > 0) {
            result.put("msg", "支付成功");
        } else {
            result.put("code", 444);
            result.put("msg", "支付失败");
        }
        return result;
    }

    @GetMapping(value = "payment/get/{id}")
    public Result getPayment(@PathVariable("id") Long id) {
        Payment payment = paymentService.getPaymentById(id);
        log.info("查询支付信息: {}", payment);
        Result result = new Result();
        if (payment != null) {
            result.put("code", 200);
            result.put("msg", "查询成功");
            result.put("data", payment);
        } else {
            result.put("code", 445);
            result.put("msg", "查询失败");
        }
        return result;
    }


    @Autowired
    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
}
