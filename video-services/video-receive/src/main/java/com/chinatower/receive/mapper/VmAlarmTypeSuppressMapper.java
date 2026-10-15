package com.chinatower.receive.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.receive.vo.entity.VmAlarmTypeSuppressVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2024/7/2 11:32
 */

@Mapper
@DS(EbikeDataBaseConstant.BASE)
public interface VmAlarmTypeSuppressMapper {

    /**
     * 查询告警类型信息
     * @param suppressVO
     * @return
     */
    List<VmAlarmTypeSuppressVO> selectAll(VmAlarmTypeSuppressVO suppressVO);
}
