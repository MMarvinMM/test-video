package com.chinatower.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.order.domain.HornDeviceDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author AutoGen
 * @since 2022-01-17
 */
@Mapper
public interface HornDeviceMapper extends BaseMapper<HornDeviceDO> {

    List<String> selectExistDeviceCodeByCodes(@Param("deviceCodeList") List<String> deviceCodeList);
}
