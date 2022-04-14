package cc.seckill.springcloud.controller;

import cc.seckill.springcloud.entities.Payment;
import cc.seckill.springcloud.entities.Result;
import cc.seckill.springcloud.service.PaymentService;
import com.netflix.appinfo.InstanceInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    @Value("${server.port}")
    private String serverPort;

    @Resource
    private DiscoveryClient discoveryClient;


    @PostMapping(value = "payment/create")
    public Result create(@RequestBody Payment payment) {
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
        log.info("当前端口 : {}", serverPort);
        Payment payment = paymentService.getPaymentById(id);
        log.info("查询支付信息: {}", payment);
        Result result = new Result();
        result.put("port", serverPort);
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

    @GetMapping(value = "/payment/discovery")
    public Object discovery() {
        List<String> services = discoveryClient.getServices();
        for (String service : services) {
            log.info("element : {}", service);
        }
        List<ServiceInstance> instances = discoveryClient.getInstances("CLOUD-PAYMENT" +
                "-SERVICE");
        for (ServiceInstance instance : instances) {
            log.info("id:{}, host:{}, port: {}, uri:{}", instance.getInstanceId(),
                    instance.getHost(), instance.getPort(), instance.getUri());
        }
        return this.discoveryClient;
    }

    @GetMapping(value = "/payment/feign/timeout")
    public String paymentFeignTimeout() {
        try {
            // 模仿超时
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return serverPort;
    }

    @GetMapping(value = "/payment/hyx/get/{id}")
    public String paymentInfoOK(@PathVariable("id") Long id) {
        String result = paymentService.paymentInfoOk(id);
        log.info("*****result : {}", result);
        return result;
    }

    @GetMapping(value = "/payment/hyx/timeout/get/{id}")
    public String paymentInfoTimeout(@PathVariable("id") Long id) {
        String result = paymentService.paymentInfoTimeout(id);
        log.info("*****result : {}", result);
        return result;
    }


    /*    setters    */


    @Autowired
    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }


}
