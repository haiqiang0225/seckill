package cc.seckill.springcloud.service;

import cc.seckill.springcloud.domain.Order;

/**
 * description: OrderService <br>
 * date: 2022/5/7 09:47 <br>
 * author: hq <br>
 * version: 1.0 <br>
 */
public interface OrderService {
    void create(Order order);
}
