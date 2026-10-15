package com.chinatower.common.core.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @ClassName OrderAlgorithmConfigDO
 * @Deacription 订购算法配置表（设备-算法订购关系，运管同步）
 * @Author chenshaowu
 * @Date 2026/07/28
 * @Version 1.0
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("vm_order_algorithm_config")
public class OrderAlgorithmConfigDO extends OrderAlgorithmConfigDomain {

}
