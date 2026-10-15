package com.chinatower.common.core.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @ClassName OrderPackageHisDO
 * @Deacription 订单套餐历史表
 * @Author chenmf
 * @Date 2022/1/6 11:09
 * @Version 1.0
 **/

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("vm_order_package_his")
public class OrderPackageHisDO extends OrderPackageDomain {

}
