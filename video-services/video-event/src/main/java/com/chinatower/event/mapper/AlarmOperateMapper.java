package com.chinatower.event.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chinatower.common.core.domain.AlarmOperateDO;
import com.chinatower.event.entity.dto.AlarmOperateDTO;
import com.chinatower.event.entity.vo.ReceiveAlarmVO;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 运营研判表 Mapper 接口
 * </p>
 *
 * @author AutoGen
 * @since 2022-02-09
 */
public interface AlarmOperateMapper extends BaseMapper<AlarmOperateDO> {

    /**
     * 更新运营研判表
     * @param map
     * @return
     */
    int updateAlarmOperate(Map map);
    /**
     * 更新运营研判表中的图片
     * @param map
     * @return
     */
    int updateAlarmOperateImg(Map map);

    /**
     * 保存运营研判真警临时表（定时补偿机制使用） vm_alarm_operate_temp_t
     * @param receiveAlarmVO
     * @return
     */
    int insertOperateTemp(ReceiveAlarmVO receiveAlarmVO);

    /**
     * 批量保存运营研判表
     * @param list
     * @return
     */
    int insertAlarmOperates(List<ReceiveAlarmVO> list);

    /**
     * 根据主键获取运营研判详情
     * @param warningOrderId
     * @return
     */
    AlarmOperateDTO getAlarmOperate(String warningOrderId);

    /**
     * 批量查询运营研判表
     * @param warningOrderIdList
     * @return
     */
    List<AlarmOperateDTO> getAlarmOperateList(@Param("list") List<String> warningOrderIdList);


    /**
     * 获取所有运营研判角色下的铁塔用户
     * @return
     */
    @DS(EbikeDataBaseConstant.BASE)
    List<String> getAlarmOperateByRole(String provinceId,String cityId);
}
