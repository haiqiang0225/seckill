package cc.seckill.springcloud.service.impl;

import cc.seckill.springcloud.dao.OrderMapper;
import cc.seckill.springcloud.domain.Order;
import cc.seckill.springcloud.service.AccountService;
import cc.seckill.springcloud.service.OrderService;
import cc.seckill.springcloud.service.StorageService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * description: OrderServiceImpl <br>
 * date: 2022/5/7 09:48 <br>
 * author: hq <br>
 * version: 1.0 <br>
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Resource
    private AccountService accountService;

    @Resource
    private StorageService storageService;

    @Resource
    private OrderMapper orderMapper;

    @Override
    @GlobalTransactional(name = "test_global_xid", rollbackFor = Exception.class)
    public void create(Order order) {
        log.info("订单信息: {}", order);
        orderMapper.insert(order);
        log.info("订单微服务开始调用库存服务, 开始扣减库存");
        storageService.decrease(order.getProductId(), order.getCount());
        log.info("订单微服务, 扣减库存完成");

        log.info("订单微服务开始调用账号服务, 开始减余额");
        accountService.decrease(order.getUserId(), order.getMoney());
        log.info("订单微服务调用账号服务, 减余额完成");

        // 修改订单状态
        log.info("修改订单状态: {}", order.getId());
        order.setStatus(1);
        orderMapper.updateById(order);
        log.info("修改订单状态完成 status=: {}", order.getStatus());
    }
}
