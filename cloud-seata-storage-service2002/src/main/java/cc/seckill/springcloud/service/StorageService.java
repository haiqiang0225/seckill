package cc.seckill.springcloud.service;

/**
 * description: StorageService <br>
 * date: 2022/5/7 14:35 <br>
 * author: hq <br>
 * version: 1.0 <br>
 */
public interface StorageService {
    void decrease(Long productId, Integer count);
}
