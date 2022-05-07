package cc.seckill.springcloud.controller;

import cc.seckill.springcloud.domain.Order;
import cc.seckill.springcloud.entities.Result;
import cc.seckill.springcloud.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * description: OderController <br>
 * date: 2022/5/7 10:05 <br>
 * author: hq <br>
 * version: 1.0 <br>
 */
@RestController
public class OderController {
    @Resource
    private OrderService orderService;


    @GetMapping("/order/create")
    public Result create(Order order) {
        orderService.create(order);
        return new Result()
                .msg("订单创建成功")
                .code(200);
    }
}
