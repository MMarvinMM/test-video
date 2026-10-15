package com.chinatower.platform.modules.alarm.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.chinatower.platform.constants.EbikeDataBaseConstant;
import com.chinatower.platform.modules.alarm.entity.AlarmTransferConfigDto;
import com.chinatower.platform.modules.alarm.entity.AlarmTransferResultVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AlarmTransferDAO {

    /**
     * 查询告警转移配置列表
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<AlarmTransferConfigDto> getAlarmTansferConfigList();

    /**
     * 获取从未处理过得工单号集合
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<AlarmTransferResultVo> getNeverOrderIdList(@Param("list") List<AlarmTransferConfigDto> list, @Param("tableName") String tableName);

    /**
     * 获取处理过得工单号集合
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM_READONLY)
    List<AlarmTransferResultVo> getNoNeverOrderIdList(@Param("list") List<AlarmTransferConfigDto> list,@Param("tableName") String tableName);

    /**
     * 根据告警编号物联删除工单表
     * @param list
     * @param tableName
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM)
    int updateWaringOrderDeleteFlag(@Param("list") List<String> list,@Param("tableName")String tableName);

    /**
     * 根据告警编号物联删除领导批示表
     * @param list
     * @return
     */
    @DS(EbikeDataBaseConstant.PLATFORM)
    int updateLearderAuditDeleteFlag(@Param("list") List<String> list);
}
