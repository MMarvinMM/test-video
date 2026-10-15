package com.chinatower.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.common.core.domain.OrderDeviceHisDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName OrderDeviceHisMapper
 * @Deacription
 * @Author chenmf
 * @Date 2022/1/6 14:14
 * @Version 1.0
 **/
@Mapper
public interface OrderDeviceHisMapper extends BaseMapper<OrderDeviceHisDO> {

    int batchInsert(@Param("list") List<OrderDeviceHisDO> list);
}
