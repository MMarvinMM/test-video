package com.chinatower.receive.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.receive.vo.entity.VmAlarmAreaSuppressVO;
import com.chinatower.receive.vo.entity.VmAlarmUavAreaSuppressVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: songxinfeng
 * @Date: 2024/7/2 11:19
 */

@Mapper
@DS(EbikeDataBaseConstant.BASE)
public interface VmAlarmAreaSuppressMapper {

    /**
     * 查询区域压制信息
     * @param suppressVO
     * @return
     */
    List<VmAlarmAreaSuppressVO> selectAll(VmAlarmAreaSuppressVO suppressVO);

    /**
     * 查询区域压制信息
     * @param suppressVO
     * @return
     */
    List<VmAlarmUavAreaSuppressVO> selectUavAll(VmAlarmAreaSuppressVO suppressVO);

}
