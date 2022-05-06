package cc.seckill.springcloud.handler;

import cc.seckill.springcloud.entities.Result;
import com.alibaba.csp.sentinel.slots.block.BlockException;

/**
 * description: CustomerBlockHandler <br>
 * date: 2022/5/5 16:12 <br>
 * author: hq <br>
 * version: 1.0 <br>
 */
public class CustomerBlockHandler {

    public static Result handlerException(BlockException exception) {
        Result result = new Result();
        result.code(444);
        result.msg("全局自定义Handler");
        return result;
    }
}
