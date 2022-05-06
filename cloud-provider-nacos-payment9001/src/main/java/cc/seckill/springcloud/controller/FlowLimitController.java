package cc.seckill.springcloud.controller;

import cc.seckill.springcloud.entities.Payment;
import cc.seckill.springcloud.entities.Result;
import cc.seckill.springcloud.handler.CustomerBlockHandler;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * description: FlowLimitController <br>
 * date: 2022/5/4 17:36 <br>
 * author: hq <br>
 * version: 1.0 <br>
 */
@RestController
public class FlowLimitController {

    @GetMapping("/byResource")
    @SentinelResource(value = "byResource", blockHandler = "byResourceHandler")
    public Result byResource() {
        Result result = new Result();
        result.put("msg", "按照资源测试");
        result.put("data", new Payment(2022L, "serial0"));
        return result;
    }

    @GetMapping("/testDefaultHandler")
    @SentinelResource(value = "testDefaultHandler", blockHandlerClass = CustomerBlockHandler.class)
    public Result byResourceHandler(BlockException exception) {
        Result result = new Result();
        result.code(499);
        result.put("msg", "服务不可用");
        return result;
    }
}
