package cc.seckill.springcloud.service;

import cc.seckill.springcloud.BaseTest;
import cc.seckill.springcloud.dao.StorageMapper;
import cc.seckill.springcloud.domain.Storage;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * description: StorageServiceTest <br>
 * date: 2022/5/7 14:57 <br>
 * author: hq <br>
 * version: 1.0 <br>
 */
@SpringBootTest()
public class StorageServiceTest extends BaseTest {
    @Resource
    private StorageMapper storageMapper;


    @Test
    public void storageDecreaseTest() {
        Storage storage = storageMapper.selectById(1);
        storage.setUsed(storage.getUsed() + 1);
        storage.setResidue(storage.getResidue() - 1);
        int i = storageMapper.updateById(storage);
        System.out.println(storage.getTotal());
    }
}
