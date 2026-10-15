package com.chinatower.common.core.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @ClassName OrderAlgorithmsHisDO
 * @Deacription 订单算法信息历史表
 * @Author chenmf
 * @Date 2022/1/6 11:14
 * @Version 1.0
 **/

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("vm_order_algorithms_his")
public class OrderAlgorithmsHisDO extends OrderAlgorithmsDomain {
}
