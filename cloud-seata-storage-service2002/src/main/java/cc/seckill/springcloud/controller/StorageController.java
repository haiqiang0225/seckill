package cc.seckill.springcloud.controller;

import cc.seckill.springcloud.entities.Result;
import cc.seckill.springcloud.service.StorageService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * description: StorageController <br>
 * date: 2022/5/7 15:53 <br>
 * author: hq <br>
 * version: 1.0 <br>
 */
@RestController
public class StorageController {
    @Resource
    private StorageService storageService;


    @RequestMapping("/storage/decrease")
    public Result decrease(Long productId, Integer count) {
        storageService.decrease(productId, count);
        return new Result()
                .code(200)
                .msg("扣减库存成功");
    }
}
