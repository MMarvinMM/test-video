package com.chinatower.common.core.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @ClassName OrderPackageDO
 * @Deacription 订单套餐表
 * @Author chenmf
 * @Date 2022/1/6 11:09
 * @Version 1.0
 **/

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("vm_order_package")
public class OrderPackageDO extends OrderPackageDomain {

    public static final int STATUS_UPDATE = 0;
    public static final int STATUS_ADD = 1;
    public static final int STATUS_DEL = 3;

}
