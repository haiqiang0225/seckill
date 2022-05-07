package cc.seckill.springcloud.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * description: Storage <br>
 * date: 2022/5/7 14:31 <br>
 * author: hq <br>
 * version: 1.0 <br>
 */
@Data
@TableName(value = "t_storage")
public class Storage {
    private Long id;

    private Long productId;

    private Integer total;

    private Integer used;

    private Integer residue;
}
