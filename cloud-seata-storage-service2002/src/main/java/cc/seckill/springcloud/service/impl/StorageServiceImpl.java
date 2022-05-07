package cc.seckill.springcloud.service.impl;

import cc.seckill.springcloud.dao.StorageMapper;
import cc.seckill.springcloud.domain.Storage;
import cc.seckill.springcloud.service.StorageService;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * description: StorageServiceImpl <br>
 * date: 2022/5/7 14:35 <br>
 * author: hq <br>
 * version: 1.0 <br>
 */
@Service
@Slf4j
public class StorageServiceImpl implements StorageService {

    @Resource
    private StorageMapper storageMapper;

    @Override
    public void decrease(Long productId, Integer count) {
        log.info("------->storage-service 中扣减库存开始 ");
        Storage storage = storageMapper.selectById(productId);
        storage.setUsed(storage.getUsed() + count);
        storage.setResidue(storage.getResidue() - count);
        storageMapper.updateById(storage);
        log.info("------->storage-service 中扣减库存结束 ");
    }
}
