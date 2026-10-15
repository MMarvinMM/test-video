package com.chinatower.common.core.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @ClassName OrderCustomerHisDO
 * @Deacription 订单客户历史表
 * @Author chenmf
 * @Date 2022/1/6 10:01
 * @Version 1.0
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("vm_order_customer_his")
public class OrderCustomerHisDO extends OrderCustomerDomain {

}
