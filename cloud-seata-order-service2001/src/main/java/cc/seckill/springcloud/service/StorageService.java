package cc.seckill.springcloud.service;

import cc.seckill.springcloud.entities.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * description: StorageService <br>
 * date: 2022/5/7 09:49 <br>
 * author: hq <br>
 * version: 1.0 <br>
 */
@FeignClient(value = "seata-storage-service")
@Component
public interface StorageService {

    @PostMapping("/storage/decrease")
    Result decrease(@RequestParam("productId") Long productId,
                    @RequestParam("count") Integer count);
}
